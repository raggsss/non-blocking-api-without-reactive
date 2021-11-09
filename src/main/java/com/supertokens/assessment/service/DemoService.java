package com.supertokens.assessment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supertokens.assessment.constants.Constants;
import com.supertokens.assessment.helper.ThreadHelper;

@Service
public class DemoService {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

	@Autowired
	private RandomIDService service;
	
	public void addRandomCount(int id) {
		
		try {
			service.addCount(id);
		}
		catch(PessimisticLockingFailureException ex) {
			logger.error("Error occured PessimisticLockingFailureException", ex);
			ThreadHelper.sleep(Constants.LOCK_EXCEPTION_RETRY_AFTER_MS);
			service.addCount(id);
		}
	}
}
