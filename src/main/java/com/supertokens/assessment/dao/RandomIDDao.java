package com.supertokens.assessment.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

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
		Query query = entityManager.createQuery("SELECT COUNT(r) FROM RandomIDModel r " );
		Long count = (Long)query.getSingleResult();
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
