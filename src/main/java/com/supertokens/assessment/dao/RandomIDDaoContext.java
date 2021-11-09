package com.supertokens.assessment.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.supertokens.assessment.helper.ThreadHelper;

@Service
public class RandomIDDaoContext {

	@Value("${sleep.for.testing.purpose}")
	private boolean allowSleepForTesting;
	
	public void sleepSomeTimeForTestinPurpose(int ms) {
		
		if(allowSleepForTesting) {
			ThreadHelper.sleep(ms);
		}
	}
}
