package com.example.secureapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
public class SecureAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureAPIApplication.class, args);
	}

}