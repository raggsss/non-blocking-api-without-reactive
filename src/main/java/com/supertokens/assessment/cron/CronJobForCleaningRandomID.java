package com.supertokens.assessment.cron;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.supertokens.assessment.constants.Constants;
import com.supertokens.assessment.helper.ThreadHelper;
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

	@Scheduled(fixedDelayString = "${scheduled.fixed.delay}")
	public void cleanRandomID() {
		long currentTimeMillis = System.currentTimeMillis() / 1000;

		logger.info("Started with cleaning ids " + currentTimeMillis);
		ThreadHelper.sleep(DELAY);
		cleanRandomIDInner2();
		logger.info("Cleaning task executed " + currentTimeMillis);
	}

	@Deprecated
	public void cleanRandomIDInner() {
		List<RandomIDModel> allRandomModels = randomIDService.getAllRandomModels();
		List<Integer> ids = new ArrayList<>();
		for (RandomIDModel m : allRandomModels) {
			// remove IDs that have a count of greater than 10 and are currently an even
			// number (including 10)
			if (m.getCount() >= Constants.COUNT_THRESHOLD && m.getCount() % 2 == 0) {
				ids.add(m.getId());
			}
		}
		if (!ids.isEmpty()) {
			randomIDService.removeRandomModelsByListOfIds(ids);
		}
	}

	public void cleanRandomIDInner2() {
		List<RandomIDModel> randomModels = randomIDService.getRandomModelsByCountThreashold(Constants.COUNT_THRESHOLD);
		List<Integer> ids = randomModels.stream().map(RandomIDModel::getId).collect(Collectors.toList());

		if (!ids.isEmpty()) {
			randomIDService.removeRandomModelsByListOfIds(ids);
		}
	}
}
