package com.suyash586.TransStats.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyash586.TransStats.business.service.StatisticService;
import com.suyash586.TransStats.persistence.entity.Statistic;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

	@Autowired
	private StatisticService statisticService;

	@GetMapping
	public ResponseEntity<Statistic> findCurrent() {

		Statistic statistic = this.statisticService.findCurrent();

		ResponseEntity<Statistic> response = new ResponseEntity<Statistic>(statistic, HttpStatus.OK);

		return response;
	}

}
