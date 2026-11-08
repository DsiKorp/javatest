package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyList;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.CheckResult;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.VerificationReport;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.verification.Check;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.verification.ListContractChecks;

/**
 * Ejecuta la suite de verificación y devuelve el informe.
 *
 * <p>Las comprobaciones comunes del contrato se ejecutan contra las dos implementaciones; las
 * específicas de {@code Deque}, sólo contra {@link com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyLinkedList}.
 * El informe no falla nunca: recoge el resultado de cada comprobación para que quien evalúa
 * pueda ver exactamente qué pasa y qué no.</p>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * VerificationService servicio = new VerificationService();
 *
 * VerificationReport informe = servicio.run();
 *
 * informe.total();   // 52   comprobaciones ejecutadas
 * informe.passed();  // 52   superadas
 * informe.failed();  // 0    fallidas
 *
 * // El detalle de cada comprobación
 * for (CheckResult comprobacion : informe.checks()) {
 *     System.out.println(comprobacion.implementation() + " / " + comprobacion.name()
 *             + " → " + comprobacion.message());
 * }
 * }</pre>
 */
@Service
public class VerificationService {

	/**
	 * Crea el servicio de verificación.
	 */
	public VerificationService() {
	}

	/**
	 * Ejecuta toda la suite y devuelve el informe.
  *
	 * @return el informe completo de la suite
	 */
	public VerificationReport run() {
		List<CheckResult> results = new ArrayList<>();
		long startedAt = System.nanoTime();

		for (ListType type : ListType.values()) {
			for (Check check : ListContractChecks.common()) {
				results.add(run(type, check));
			}
			if (type == ListType.LINKED_LIST) {
				for (Check check : ListContractChecks.dequeOnly()) {
					results.add(run(type, check));
				}
			}
		}

		long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
		int passed = (int) results.stream().filter(CheckResult::passed).count();
		return new VerificationReport(results.size(), passed, results.size() - passed, durationMs, results);
	}

	private CheckResult run(ListType type, Check check) {
		long startedAt = System.nanoTime();
		try {
			MyList<Object> list = type.create();
			check.assertion().verify(list, type);
			return new CheckResult(check.name(), type.name(), true, "OK", elapsedMs(startedAt));
		}
		catch (AssertionError error) {
			return new CheckResult(check.name(), type.name(), false, String.valueOf(error.getMessage()),
					elapsedMs(startedAt));
		}
		catch (Exception error) {
			return new CheckResult(check.name(), type.name(), false,
					error.getClass().getSimpleName() + ": " + error.getMessage(), elapsedMs(startedAt));
		}
	}

	private long elapsedMs(long startedAt) {
		return (System.nanoTime() - startedAt) / 1_000_000;
	}
}
