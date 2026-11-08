package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.verification;

/**
 * Una comprobación con nombre, tal y como aparece en el informe.
 *
 * @param name nombre legible de la comprobación
 * @param assertion comprobación a ejecutar
 */
public record Check(String name, ListAssertion assertion) {
}
