package com.maquinaria_agricola;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class GestionApplication {

	public static void main(String[] args) {

		try {
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
			});
		} catch (Exception e) {
			System.err
					.println("Advertencia: No se pudo cargar el archivo .env. Usando variables de entorno existentes.");
		}

		SpringApplication.run(GestionApplication.class, args);
	}

}
