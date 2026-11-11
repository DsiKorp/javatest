package com.dsikorp.epam.javatest.common.verification;

/**
 * Resultado de una comprobación individual de la suite de verificación.
 *
 * @param name nombre de la comprobación
 * @param implementation implementación o camino sobre el que se ha ejecutado
 * @param passed si la comprobación ha sido superada
 * @param message detalle del resultado
 * @param durationMs duración de la comprobación en milisegundos
 */
public record CheckResult(String name, String implementation, boolean passed, String message, long durationMs) {
}
