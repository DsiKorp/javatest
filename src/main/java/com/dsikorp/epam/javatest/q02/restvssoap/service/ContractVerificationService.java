package com.dsikorp.epam.javatest.q02.restvssoap.service;

import java.util.List;

import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.dsikorp.epam.javatest.common.verification.CheckResult;
import com.dsikorp.epam.javatest.common.verification.VerificationReport;
import com.dsikorp.epam.javatest.common.verification.VerificationRunner;
import com.dsikorp.epam.javatest.q02.restvssoap.verification.ContractChecks;

/**
 * Ejecuta la suite de verificación de la pregunta 2 y devuelve el informe.
 */
@Service
public class ContractVerificationService {

	private final ApplicationContext applicationContext;

	/**
  * Ejecuta la suite de verificación de la pregunta 2 y devuelve el informe.
  * @param applicationContext el contexto, del que se saca el puerto real del servidor
  */
	public ContractVerificationService(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
  * Ejecuta la suite de verificación y devuelve el informe.
  *
  * @return la suite de verificación y devuelve el informe
  */
	public VerificationReport run() {
		return run(baseUrl());
	}

	/**
  * Ejecuta la suite de verificación y devuelve el informe.
  *
  * @param baseUrl el valor de baseUrl
  * @return la suite de verificación y devuelve el informe
  */
	public VerificationReport run(String baseUrl) {
		long startedAt = System.nanoTime();
		List<CheckResult> results = VerificationRunner.run(ContractChecks.implementation(),
				ContractChecks.checks(baseUrl));
		long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
		return VerificationRunner.toReport(results, durationMs);
	}

	/**
  * Devuelve la URL base del propio servidor.
  *
  * @return la URL base del propio servidor
  */
	public String baseUrl() {
		if (applicationContext instanceof ServletWebServerApplicationContext webContext) {
			int port = webContext.getWebServer().getPort();
			String contextPath = webContext.getServletContext().getContextPath();
			return "http://localhost:" + port + contextPath;
		}
		return "http://localhost:8080/api";
	}
}
