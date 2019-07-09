package com.zendaimoney.coreaccount.front.cfg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

	@Bean
	public ExecutorService threadPool() {
		return Executors.newSingleThreadExecutor();

	}
}