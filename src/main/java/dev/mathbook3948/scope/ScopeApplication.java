package dev.mathbook3948.scope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ScopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScopeApplication.class, args);
	}

}
