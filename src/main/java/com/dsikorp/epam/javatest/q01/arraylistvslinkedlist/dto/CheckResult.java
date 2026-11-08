package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto;

/**
 * Resultado de una comprobación individual de la suite de verificación.
 *
 * @param name nombre de la comprobación
 * @param implementation implementación de lista sobre la que se ha ejecutado
 * @param passed si la comprobación ha sido superada
 * @param message detalle del resultado
 * @param durationMs duración de la comprobación en milisegundos
 */
public record CheckResult(String name, String implementation, boolean passed, String message, long durationMs) {
}
