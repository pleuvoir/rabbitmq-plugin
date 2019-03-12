package io.github.pleuvoir.springboot.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 */
@SpringBootApplication
public class SpringbootExampleLauncher {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SpringbootExampleLauncher.class);
		application.run(args);
	}

}