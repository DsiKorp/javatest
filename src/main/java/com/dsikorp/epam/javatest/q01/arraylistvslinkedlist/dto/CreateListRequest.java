package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto;

import java.util.List;

/**
 * Petición de creación de una lista.
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * // {"values":[1, "a", null]}
 * CreateListRequest peticion = new CreateListRequest(List.of(1, "a", null));
 *
 * // {} → la lista se crea vacía
 * CreateListRequest vacia = new CreateListRequest(null);
 * }</pre>
 *
 * @param values valores iniciales; {@code null} equivale a una lista vacía
 */
public record CreateListRequest(List<Object> values) {
}
