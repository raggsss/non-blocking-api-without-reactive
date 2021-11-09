package com.supertokens.assessment.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.supertokens.assessment.model.RandomIDModel;

@Repository
public class RandomIDDao {
	
	private static final Logger logger = LoggerFactory.getLogger(RandomIDDao.class);
	
	private static String PERSISTENT_LOCK_TIMEOUT = "javax.persistence.lock.timeout";
	
	@Value("${persistent.lock.timeout}")
	private long persistentLockTimeout;
	
	@Autowired
	private RandomIDDaoContext randomIDDaoContext;

	@PersistenceContext
	private EntityManager entityManager;

	public List<RandomIDModel> getAllRandomModels() {
		CriteriaQuery<RandomIDModel> criteria = entityManager.getCriteriaBuilder().createQuery(RandomIDModel.class);
		criteria.select(criteria.from(RandomIDModel.class));
		List<RandomIDModel> listOfRandomModels = entityManager.createQuery(criteria).getResultList();
		return listOfRandomModels;
	}

	public List<RandomIDModel> getRandomModelsByCountThreshold(int countThreshold) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<RandomIDModel> criteriaQuery = criteriaBuilder.createQuery(RandomIDModel.class);

		Root<RandomIDModel> root = criteriaQuery.from(RandomIDModel.class);

		Predicate countPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("count"), countThreshold);
		Predicate modPredicate = criteriaBuilder.equal(criteriaBuilder.mod(root.get("count"), 2), 0);

		Predicate finalPredicate = criteriaBuilder.and(countPredicate, modPredicate);
		criteriaQuery.where(finalPredicate);

		List<RandomIDModel> listOfRandomModels = entityManager.createQuery(criteriaQuery).getResultList();
		return listOfRandomModels;
	}

	public RandomIDModel getRandomIDModel(int id) {
		//Lock can also be added here, but created another method to do so.
		return entityManager.find(RandomIDModel.class, id);
	}
	
	/**
	 * Pessimistic lock applied while selecting a record
	 * @param id
	 * @return
	 */
	public RandomIDModel getRandomIDModelMakePessimisticWriteLockOnById(int id) {
		
		logger.info("Trying to obtain pessimistic lock...");

        Query query = entityManager.createQuery("select model from RandomIDModel model where model.id = :id");
        query.setParameter("id", id);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        query.setHint(PERSISTENT_LOCK_TIMEOUT, String.valueOf(persistentLockTimeout));
        
        RandomIDModel model = null;
        try {
        	model = (RandomIDModel)query.getSingleResult();
        }
        catch(NoResultException ex) {
        	//Do nothing, as record does not exist in the db.
        }
        
        randomIDDaoContext.sleepSomeTimeForTestinPurpose(800);

        logger.info(">>pessimistic lock obtained>>");

        return model;
	}

	public RandomIDModel saveRandomIDModel(RandomIDModel model) {
		return entityManager.merge(model);
	}

	public void saveRandomIDModelAndFlush(RandomIDModel model) {
		entityManager.persist(model);
		flushAndClear();
	}

	public void removeRandomIDModel(RandomIDModel model) {
		entityManager.remove(model);
	}

	public Long getCount() {
		Query query = entityManager.createQuery("SELECT COUNT(r) FROM RandomIDModel r ");
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public int bulkRemove(List<Integer> ids) {
		Query query = entityManager.createQuery("DELETE FROM RandomIDModel rd where rd.id in (?1)");
		query.setParameter(1, ids);
		return query.executeUpdate();
	}

	void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}
