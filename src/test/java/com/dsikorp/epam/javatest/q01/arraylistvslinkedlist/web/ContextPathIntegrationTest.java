package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Comprueba el {@code server.servlet.context-path} de verdad, con un servidor real.
 *
 * <p>El slice web ({@link MyListControllerTest}) usa MockMvc, que manda la petición
 * directamente al dispatcher y por eso no ve el context-path. Este test, en cambio, levanta
 * Tomcat en un puerto real y habla HTTP, así que sí comprueba la URL pública completa.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContextPathIntegrationTest {

	@LocalServerPort
	private int port;

	private final HttpClient client = HttpClient.newHttpClient();

	private HttpResponse<String> get(String path) throws Exception {
		HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	@Test
	@DisplayName("las URLs públicas llevan el prefijo del context-path")
	void publicUrlsCarryTheContextPath() throws Exception {
		assertThat(get("/api/q1/lists").statusCode()).isEqualTo(200);
		assertThat(get("/api/q1/verify").statusCode()).isEqualTo(200);
	}

	@Test
	@DisplayName("sin el prefijo no hay nada: el context-path es obligatorio")
	void urlsWithoutThePrefixDoNotExist() throws Exception {
		assertThat(get("/lists").statusCode()).isEqualTo(404);
		assertThat(get("/verify").statusCode()).isEqualTo(404);
	}
}
