package com.dsikorp.epam.javatest.common.verification;

import java.util.List;

/**
 * Informe completo de una suite de verificación.
 *
 * @param total número de comprobaciones ejecutadas
 * @param passed número de comprobaciones superadas
 * @param failed número de comprobaciones fallidas
 * @param durationMs duración total de la suite en milisegundos
 * @param checks detalle de cada comprobación
 */
public record VerificationReport(int total, int passed, int failed, long durationMs, List<CheckResult> checks) {
}
