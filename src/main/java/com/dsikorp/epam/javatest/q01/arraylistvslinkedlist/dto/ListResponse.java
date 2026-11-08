package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto;

import java.util.List;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;

/**
 * Estado completo de una lista.
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * // {"id":"9eff4923-e21f-4333-87d3-d1e8d15e99e2","type":"ARRAY_LIST",
 * //  "size":3,"values":[1,"a",null]}
 * ListResponse respuesta = new ListResponse("9eff4923-e21f-4333-87d3-d1e8d15e99e2",
 *         ListType.ARRAY_LIST, 3, List.of(1, "a", null));
 * }</pre>
 *
 * @param id identificador de la lista
 * @param type implementación que respalda la lista
 * @param size número de elementos
 * @param values contenido actual de la lista
 */
public record ListResponse(String id, ListType type, int size, List<Object> values) {
}
