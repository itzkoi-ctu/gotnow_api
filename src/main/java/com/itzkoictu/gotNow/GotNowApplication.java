package com.itzkoictu.gotNow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.itzkoictu.gotNow.repository") // Đảm bảo đúng package

public class GotNowApplication {

	public static void main(String[] args) {
		SpringApplication.run(GotNowApplication.class, args);
	}

}
