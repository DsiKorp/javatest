package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto;

import java.util.List;

/**
 * Informe completo de la suite de verificación.
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * // {"total":52,"passed":52,"failed":0,"durationMs":15,"checks":[ ... ]}
 * VerificationReport informe = new VerificationReport(52, 52, 0, 15, List.of(
 *         new CheckResult("una lista nueva está vacía", "ARRAY_LIST", true, "OK", 0),
 *         new CheckResult("clear vacía la lista", "LINKED_LIST", true, "OK", 0)));
 * }</pre>
 *
 * @param total número de comprobaciones ejecutadas
 * @param passed número de comprobaciones superadas
 * @param failed número de comprobaciones fallidas
 * @param durationMs duración total de la suite en milisegundos
 * @param checks detalle de cada comprobación
 */
public record VerificationReport(int total, int passed, int failed, long durationMs, List<CheckResult> checks) {
}
