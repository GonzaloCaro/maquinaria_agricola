package com.maquinaria_agricola.gestion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // <--- Agrega esto
class GestionApplicationTests {

	@Test
	void contextLoads() {
		// Esta prueba simplemente verifica que el contexto de Spring (Base de datos,
		// Beans, etc.)
		// inicia correctamente sin errores.
	}

}