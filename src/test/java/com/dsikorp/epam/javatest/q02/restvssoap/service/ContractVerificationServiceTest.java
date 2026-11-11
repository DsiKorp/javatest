package com.dsikorp.epam.javatest.q02.restvssoap.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.dsikorp.epam.javatest.common.verification.CheckResult;
import com.dsikorp.epam.javatest.common.verification.VerificationReport;

/**
 * La suite de la pregunta 2 debe salir siempre en verde: si aparece un fallo, es que los dos
 * contratos han dejado de describir la misma operación, o que un stub ha dejado de ir a donde
 * dice el contrato.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContractVerificationServiceTest {

	@LocalServerPort
	private int port;

	@Autowired
	private ContractVerificationService verificationService;

	@Test
	@DisplayName("todas las comprobaciones se superan")
	void allChecksPass() {
		VerificationReport report = verificationService.run("http://localhost:" + port + "/api");

		assertThat(report.failed()).as("comprobaciones fallidas: %s", failing(report)).isZero();
		assertThat(report.passed()).isEqualTo(report.total());
		assertThat(report.total()).isGreaterThan(0);
	}

	@Test
	@DisplayName("el informe cubre los contratos, los stubs y la equivalencia REST/SOAP")
	void reportCoversEverything() {
		VerificationReport report = verificationService.run("http://localhost:" + port + "/api");

		assertThat(report.checks()).extracting(CheckResult::name)
			.anySatisfy(name -> assertThat(name).contains("WSDL"))
			.anySatisfy(name -> assertThat(name).contains("OpenAPI"))
			.anySatisfy(name -> assertThat(name).contains("mismo resultado"));
	}

	private static String failing(VerificationReport report) {
		return report.checks()
			.stream()
			.filter(check -> !check.passed())
			.map(check -> check.name() + " -> " + check.message())
			.toList()
			.toString();
	}
}
