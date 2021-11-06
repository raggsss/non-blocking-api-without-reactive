package com.supertokens.assessment.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.supertokens.assessment.model.RandomIDModel;

@Repository
public class RandomIDDao {

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
		return entityManager.find(RandomIDModel.class, id);
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
