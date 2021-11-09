package com.supertokens.assessment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supertokens.assessment.service.RandomIDService;

@RestController
@RequestMapping(path = "/")
public class RandomController 
{
	
	@Autowired
	private RandomIDService service;
	
	@GetMapping(path="/", produces = "application/json")
	public String getRandom() {
		return "hello world";
	}
	
	@PutMapping(path="/{id}", produces = "application/json")
	public void setRandom(@PathVariable int id) {
		service.addCount(id);
	}
}
