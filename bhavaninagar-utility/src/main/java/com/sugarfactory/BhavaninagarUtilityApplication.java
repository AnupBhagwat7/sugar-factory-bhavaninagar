package com.sugarfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories("com.sugarfactory.repository")
@EntityScan("com.sugarfactory.model")
@SpringBootApplication
public class BhavaninagarUtilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(BhavaninagarUtilityApplication.class, args);
	}

}
