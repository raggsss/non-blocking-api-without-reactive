package com.supertokens.assessment.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supertokens.assessment.constants.Constants;
import com.supertokens.assessment.cron.CronJobForCleaningRandomID;
import com.supertokens.assessment.dao.RandomIDDao;
import com.supertokens.assessment.model.RandomIDModel;

@Service
public class RandomIDServiceImpl implements RandomIDService {
	
	private static final Logger logger = LoggerFactory.getLogger(CronJobForCleaningRandomID.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private RandomIDDao randomIDDao;

	@Override
	public long getCount(int id) {
		return 0L;
	}

	@Transactional
	@Override
	public void addCount(int id) {
		
		RandomIDModel rm = randomIDDao.getRandomIDModel(id);
		if(rm == null) {
			rm = new RandomIDModel();
			rm.setCount(id);
			logger.info("Adding/updating random number with count "+id);
		}
		else {
			logger.info("Adding/updating random number with id "+id);
			if(rm.getCount() % 2 == 0) {
				rm.setCount(rm.getCount() + Constants.INCREMENT_FOR_EVEN);
			}
			else {
				rm.setCount(rm.getCount() + Constants.INCREMENT_FOR_ODD);
			}
		}
		
		randomIDDao.saveRandomIDModel(rm);
	}
	
	@Transactional
	@Override
	public int removeRandomModelsByListOfIds(List<Integer> ids) {
		return randomIDDao.bulkRemove(ids);
	}

	@Override
	public List<RandomIDModel> getAllRandomModels() {
		return randomIDDao.getAllRandomModels();
	}

	@Override
	public List<RandomIDModel> getRandomModelsByCountThreashold(int countThreshold) {
		return randomIDDao.getRandomModelsByCountThreshold(countThreshold);
	}
}
