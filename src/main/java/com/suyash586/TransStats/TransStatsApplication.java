package com.suyash586.TransStats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan
@EnableSwagger2
@EnableAsync
public class TransStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransStatsApplication.class, args);
	}

}
