package it.dstech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StarterClass {

	public static void main(String[] args) {
		SpringApplication.run(StarterClass.class, args);
		
	}
}
