package com.zendaimoney.coreaccount.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommonConfig {

	@Bean
	public ExecutorService executor() {
		return Executors.newSingleThreadExecutor();

	}
}