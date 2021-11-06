package com.supertokens.assessment;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.supertokens.assessment.SpringBootDemoApplication;
import com.supertokens.assessment.constants.Constants;
import com.supertokens.assessment.cron.CronJobForCleaningRandomID;
import com.supertokens.assessment.dao.RandomIDDao;
import com.supertokens.assessment.model.RandomIDModel;
import com.supertokens.assessment.service.RandomIDService;
import com.supertokens.assessment.service.RandomIDServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootDemoApplication.class)
public class RandomIDSpringTest {

	@SpyBean
	private CronJobForCleaningRandomID cronJob;

	@Mock
	private RandomIDService randomIDService;

	@Mock
	private RandomIDDao randomIDDao;

	@Mock
	private RandomIDModel rmd;

	@Before
	public void setUp() {
		reset(randomIDDao);
		reset(randomIDService);
	}

	@Test
	public void addCountTestForEven() {

		int randomID = 2;
		rmd.setId(randomID);
		rmd.setCount(randomID);
		when(randomIDDao.getRandomIDModel(randomID)).thenReturn(rmd);
		randomIDService.addCount(2);
		when(rmd.getCount()).thenReturn(randomID + Constants.INCREMENT_FOR_EVEN);
		assertEquals((rmd.getCount()), (randomID + Constants.INCREMENT_FOR_EVEN));
	}

	@Test
	public void addCountTestForOdd() {

		int randomID = 3;
		rmd.setId(randomID);
		rmd.setCount(randomID);
		when(randomIDDao.getRandomIDModel(randomID)).thenReturn(rmd);
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
			{
				add(new RandomIDModel(1, 12));
				add(new RandomIDModel(2, 22));
				add(new RandomIDModel(3, 16));
				add(new RandomIDModel(4, 20));
			}
		};

		ArrayList<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		ids.add(4);

		when(randomIDService.getRandomModelsByCountThreashold(10)).thenReturn(rmds);
		cronJob.cleanRandomIDInner2();
		verify(randomIDService, times(1)).removeRandomModelsByListOfIds(ids);
	}

	@Test
	public void whenWaitFiveSecondVerifyCleanRandomIDIsCalledAtLeastTwoTimes() {
		await().atMost(Duration.FIVE_SECONDS).untilAsserted(() -> verify(cronJob, atLeast(2)).cleanRandomID());
	}
}
