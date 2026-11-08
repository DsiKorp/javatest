package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto;

/**
 * Resultado de buscar un elemento.
 *
 * @param index posición de la primera aparición, o {@code -1} si no aparece
 * @param found si el elemento está presente
 */
public record IndexOfResponse(int index, boolean found) {
}
