package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import java.util.List;

/**
 * Marcador de las listas implementadas desde cero en este proyecto.
 *
 * <p>No añade ningún método: sólo deja explícito, en las firmas del servicio y de los DTOs, que
 * se está trabajando con una implementación propia y no con {@code java.util.ArrayList} o
 * {@code java.util.LinkedList}.</p>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * MyList<String> lista = new MyArrayList<>();
 * lista.add("a");
 *
 * // Al declararla como MyList se sigue viendo como un java.util.List corriente:
 * List<String> comoLista = lista;
 * comoLista.indexOf("a");   // 0
 * }</pre>
 *
 * @param <E> tipo de los elementos
 */
public interface MyList<E> extends List<E> {
}
