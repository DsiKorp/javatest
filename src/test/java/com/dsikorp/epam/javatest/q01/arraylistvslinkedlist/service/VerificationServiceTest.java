package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * La suite de verificación que se expone por HTTP debe salir siempre en verde: si aparece un
 * fallo, es que las implementaciones han roto el contrato.
 */
class VerificationServiceTest {

	private final VerificationService verificationService = new VerificationService();

	@Test
	@DisplayName("todas las comprobaciones se superan en las dos implementaciones")
	void allChecksPass() {
		var report = verificationService.run();

		assertThat(report.failed()).as("comprobaciones fallidas: %s", failing(report)).isZero();
		assertThat(report.passed()).isEqualTo(report.total());
		assertThat(report.total()).isGreaterThan(0);
	}

	@Test
	@DisplayName("el informe cubre las dos implementaciones")
	void coversBothImplementations() {
		var report = verificationService.run();

		assertThat(report.checks()).extracting("implementation")
			.contains("ARRAY_LIST", "LINKED_LIST");
		assertThat(report.checks()).allSatisfy(check -> {
			assertThat(check.passed()).isTrue();
			assertThat(check.message()).isEqualTo("OK");
			assertThat(check.name()).isNotBlank();
		});
	}

	@Test
	@DisplayName("las comprobaciones específicas de Deque sólo se ejecutan sobre la lista enlazada")
	void dequeChecksOnlyRunOnLinkedList() {
		var report = verificationService.run();

		assertThat(report.checks())
			.filteredOn(check -> check.name().contains("FIFO"))
			.extracting("implementation")
			.containsOnly("LINKED_LIST");
	}

	private static String failing(com.dsikorp.epam.javatest.common.verification.VerificationReport report) {
		return report.checks()
			.stream()
			.filter(check -> !check.passed())
			.map(check -> check.implementation() + " / " + check.name() + " -> " + check.message())
			.toList()
			.toString();
	}
}
