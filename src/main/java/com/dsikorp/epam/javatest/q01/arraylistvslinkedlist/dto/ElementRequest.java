package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto;

/**
 * Petición para insertar o reemplazar un elemento.
 *
 * <p>El valor puede ser {@code null}: {@code null} es un elemento legítimo de una lista.</p>
 *
 * @param value valor a insertar o reemplazar
 */
public record ElementRequest(Object value) {
}
