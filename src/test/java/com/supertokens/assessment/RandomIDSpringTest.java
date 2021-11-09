package com.supertokens.assessment;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.awaitility.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.supertokens.assessment.constants.Constants;
import com.supertokens.assessment.cron.CronJobForCleaningRandomID;
import com.supertokens.assessment.dao.RandomIDDao;
import com.supertokens.assessment.model.RandomIDModel;
import com.supertokens.assessment.service.RandomIDService;
import com.supertokens.assessment.service.RandomIDServiceImpl;

public class RandomIDSpringTest extends BaseTest {

	@SpyBean
	private CronJobForCleaningRandomID cronJob;
	
	@Mock
	private RandomIDDao randomIDDao;

	@Mock
	private RandomIDModel rmd;
	
	@InjectMocks
	private RandomIDServiceImpl randomIDService;
	
	@Before
	public void setUpAll() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void setUp() {
		reset(randomIDDao);
	}

	@Test
	public void addCountTestForEven() {

		int randomID = 2;
		rmd.setId(randomID);
		rmd.setCount(randomID);
		when(randomIDDao.getRandomIDModelMakePessimisticWriteLockOnById(anyInt())).thenReturn(rmd);
		randomIDService.addCount(2);
		when(rmd.getCount()).thenReturn(randomID + Constants.INCREMENT_FOR_EVEN);
		assertEquals((rmd.getCount()), (randomID + Constants.INCREMENT_FOR_EVEN));
	}

	@Test
	public void addCountTestForOdd() {

		int randomID = 3;
		rmd.setId(randomID);
		rmd.setCount(randomID);
		when(randomIDDao.getRandomIDModelMakePessimisticWriteLockOnById(randomID)).thenReturn(rmd);
		randomIDService.addCount(2);
		when(rmd.getCount()).thenReturn(randomID + Constants.INCREMENT_FOR_ODD);
		assertEquals((rmd.getCount()), (randomID + Constants.INCREMENT_FOR_ODD));
	}

	@Test
	public void cleanRandomIDInnerTest() {

		RandomIDService randomIDService = mock(RandomIDServiceImpl.class);
		CronJobForCleaningRandomID cronJob = spy(CronJobForCleaningRandomID.class);
		cronJob.setRandomIDService(randomIDService);

		List<RandomIDModel> rmds = new ArrayList<RandomIDModel>() {
			private static final long serialVersionUID = 1L;

			{
				add(new RandomIDModel(1, 12));
				add(new RandomIDModel(2, 22));
				add(new RandomIDModel(3, 16));
				add(new RandomIDModel(4, 20));
			}
		};

		ArrayList<Integer> ids = new ArrayList<Integer>() {{
			add(1);
			add(2);
			add(3);
			add(4);
		}};

		when(randomIDService.getRandomModelsByCountThreashold(10)).thenReturn(rmds);
		cronJob.cleanRandomIDInner2();
		verify(randomIDService, times(1)).removeRandomModelsByListOfIds(ids);
	}

	@Test
	public void whenWaitFiveSecondVerifyCleanRandomIDIsCalledAtLeastTwoTimes() {
		await().atMost(Duration.FIVE_SECONDS).untilAsserted(() -> verify(cronJob, atLeast(2)).cleanRandomID());
	}
}
