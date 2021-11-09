package com.supertokens.assessment;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.supertokens.assessment.dao.RandomIDDao;
import com.supertokens.assessment.service.DemoService;
import com.supertokens.assessment.service.RandomIDServiceImpl;

public class RandomIDPessimisticLockingTest extends BaseTest {
	
	@SpyBean
	private DemoService demoService;
	
	@SpyBean
	private RandomIDServiceImpl randomIDService;
	
	@SpyBean
	private RandomIDDao randomIDDao;

	@Test
	public void randomIDWriteLockTest() throws InterruptedException {
		
		List<Integer> countIds = Arrays.asList(8, 10, 11, 12);
        final ExecutorService executor = Executors.newFixedThreadPool(countIds.size());

        for (final int id : countIds) {
            executor.execute(() -> demoService.addRandomCount(countIds.get(0)));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        
        verify(randomIDService, atLeast(countIds.size()-1)).addCount(anyInt());
	}
}
