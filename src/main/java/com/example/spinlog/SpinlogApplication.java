package com.example.spinlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpinlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpinlogApplication.class, args);
	}

}
