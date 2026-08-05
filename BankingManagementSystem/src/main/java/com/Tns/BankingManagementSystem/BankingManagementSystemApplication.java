package com.Tns.BankingManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BankingManagementSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(BankingManagementSystemApplication.class, args);
		System.out.println("Application started");
		System.out.println("Caching Strategy");
	}

}
