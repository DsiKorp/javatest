package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;

/**
 * Resumen de una lista, sin su contenido.
 *
 * @param id identificador de la lista
 * @param type implementación que respalda la lista
 * @param size número de elementos
 */
public record ListSummary(String id, ListType type, int size) {
}
