package com.salesianostriana.dam.trianafy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(description = "Rest API de Trianafy",
		version = "Gypsy Version",
		contact = @Contact(email = "gonzalez.cavic22@triana.salesianos.edu", name = "Víctor"),
		title = "Trianay API"
)
)
public class TrianafyBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrianafyBaseApplication.class, args);
	}

}
