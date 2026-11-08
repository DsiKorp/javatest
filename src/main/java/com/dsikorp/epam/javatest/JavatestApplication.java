package com.dsikorp.epam.javatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 *
 * <p>Cada punto del examen se expone como un módulo independiente bajo su propio paquete
 * ({@code qNN/<tema>}), con sus propios endpoints y su propio endpoint de autoverificación.</p>
 */
@SpringBootApplication
public class JavatestApplication {

	/**
	 * Crea la aplicación.
	 */
	public JavatestApplication() {
	}

	/**
	 * Arranca la aplicación Spring Boot.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(JavatestApplication.class, args);
	}

}
