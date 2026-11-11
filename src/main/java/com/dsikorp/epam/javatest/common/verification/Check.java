package com.dsikorp.epam.javatest.common.verification;

/**
 * Una comprobación con nombre, tal y como aparece en el informe.
 *
 * @param name nombre legible de la comprobación
 * @param action comprobación a ejecutar
 */
public record Check(String name, CheckAction action) {
}
