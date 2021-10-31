package com.supertokens.assessment.cron;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.supertokens.assessment.constants.Constants;
import com.supertokens.assessment.model.RandomIDModel;
import com.supertokens.assessment.service.RandomIDService;

@Component
public class CronJobForCleaningRandomID {
	
	private final int DELAY = 1000;
    
    private static final Logger logger = LoggerFactory.getLogger(CronJobForCleaningRandomID.class);
    
    private RandomIDService randomIDService;

    @Autowired
	public void setRandomIDService(RandomIDService randomIDService) {
		this.randomIDService = randomIDService;
	}

	private Executor executor = Executors.newFixedThreadPool(Constants.ACTIVE_THREADS);
    private volatile boolean initialized = false;

    void initialize() {
        executor.execute(() -> {
            initialized = true;
        });
    }

    boolean isInitialized() {
        return initialized;
    }

    @Scheduled(fixedRateString = "${scheduled.fixed.rate}")
    public void cleanRandomID() {
        executor.execute(() -> {
            sleep(DELAY);
            long currentTimeMillis = System.currentTimeMillis() / 1000;
            
            logger.info("Started with cleaning ids "+currentTimeMillis);
            cleanRandomIDInner();
        	logger.info("Cleaning task executed "+currentTimeMillis);
        });
    }
    
    public void cleanRandomIDInner() {
    	List<RandomIDModel> allRandomModels = randomIDService.getAllRandomModels();
    	List<Integer> ids = new ArrayList<>();
    	for(RandomIDModel m: allRandomModels) {
    		//remove IDs that have a count of greater than 10 and are currently an even number (including 10)
    		if(m.getCount() >= Constants.COUNT_THRESHOLD && m.getCount() % 2 == 0) {
    			ids.add(m.getId());
    		}
    	}
    	if(!ids.isEmpty()) {
    		randomIDService.removeRandomModelsByListOfIds(ids);
    	}
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }
}
