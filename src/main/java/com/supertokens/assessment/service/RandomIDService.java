package com.supertokens.assessment.service;

import java.util.List;

import com.supertokens.assessment.model.RandomIDModel;

public interface RandomIDService {
	public long getCount(int id);
	public void addCount(int id);
	public int removeRandomModelsByListOfIds(List<Integer> ids);
	public List<RandomIDModel> getAllRandomModels();
	public List<RandomIDModel> getRandomModelsByCountThreashold(int countThreshold);
}