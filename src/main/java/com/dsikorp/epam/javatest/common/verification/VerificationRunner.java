package com.dsikorp.epam.javatest.common.verification;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejecuta una suite de comprobaciones y construye su informe.
 *
 * <p>El informe nunca "falla": recoge el resultado de cada comprobación para que quien evalúa
 * pueda ver exactamente qué pasa y qué no.</p>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * List<CheckResult> resultados = VerificationRunner.run("ARRAY_LIST", comprobaciones);
 * VerificationReport informe = VerificationRunner.toReport(resultados, 15);
 *
 * informe.total();   // 52
 * informe.passed();  // 52
 * informe.failed();  // 0
 * }</pre>
 */
public final class VerificationRunner {

	private VerificationRunner() {
	}

	/**
  * Ejecuta las comprobaciones y devuelve el detalle de cada una.
  *
  * @param implementation etiqueta de la implementación sobre la que se ejecuta
  * @param checks comprobaciones a ejecutar
  * @return el resultado de cada comprobación, en el mismo orden
  */
	public static List<CheckResult> run(String implementation, List<Check> checks) {
		List<CheckResult> results = new ArrayList<>();
		for (Check check : checks) {
			long startedAt = System.nanoTime();
			try {
				check.action().verify();
				results.add(new CheckResult(check.name(), implementation, true, "OK", elapsedMs(startedAt)));
			}
			catch (AssertionError error) {
				results.add(new CheckResult(check.name(), implementation, false, String.valueOf(error.getMessage()),
						elapsedMs(startedAt)));
			}
			catch (Exception error) {
				results.add(new CheckResult(check.name(), implementation, false,
						error.getClass().getSimpleName() + ": " + error.getMessage(), elapsedMs(startedAt)));
			}
		}
		return results;
	}

	/**
  * Construye el informe a partir del detalle de las comprobaciones.
  *
  * @param results resultado de cada comprobación
  * @param durationMs duración total de la suite en milisegundos
  * @return el informe
  */
	public static VerificationReport toReport(List<CheckResult> results, long durationMs) {
		int passed = (int) results.stream().filter(CheckResult::passed).count();
		return new VerificationReport(results.size(), passed, results.size() - passed, durationMs, results);
	}

	private static long elapsedMs(long startedAt) {
		return (System.nanoTime() - startedAt) / 1_000_000;
	}
}
