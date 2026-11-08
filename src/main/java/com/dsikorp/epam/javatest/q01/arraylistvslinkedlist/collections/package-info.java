/**
 * Implementaciones propias de {@code List}: {@link com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyArrayList}
 * y {@link com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyLinkedList}.
 *
 * <h2>Regla de oro de este paquete</h2>
 * <p>Ninguna clase de este paquete puede apoyarse en las colecciones de {@code java.util} para
 * resolver su comportamiento. Concretamente están <strong>prohibidos</strong>:
 * {@code java.util.ArrayList}, {@code java.util.LinkedList}, {@code java.util.Collections},
 * {@code java.util.Arrays}, {@code java.util.HashMap} y cualquier otro tipo de
 * {@code java.util} que sea una implementación de colección (además de los métodos
 * {@code List.of(...)}, {@code Arrays.copyOf(...)}, {@code Arrays.sort(...)} o
 * {@code Collections.sort(...)}).</p>
 *
 * <p><strong>Permitidos</strong>: las <em>interfaces</em> {@code java.util.List},
 * {@code java.util.Collection}, {@code java.util.Iterator}, {@code java.util.ListIterator},
 * {@code java.util.RandomAccess}, {@code java.util.Deque} y {@code java.util.Comparator};
 * las excepciones del JDK ({@code NoSuchElementException},
 * {@code IndexOutOfBoundsException}, {@code ConcurrentModificationException}); y las utilidades
 * de soporte {@code java.util.Objects}, {@code java.util.Spliterators},
 * {@code java.lang.reflect.Array} y {@code System.arraycopy}.</p>
 *
 * <p>Fuera de este paquete (capa web, servicio y DTOs) el uso de las colecciones estándar de
 * {@code java.util} es normal y necesario: Spring, Jackson y el registro en memoria las
 * necesitan. La frontera está aquí, y sólo aquí.</p>
 *
 * <p><strong>Ejemplo de la frontera.</strong> Implementar las interfaces del JDK está permitido
 * (lo que no lo está es delegar en sus implementaciones):</p>
 * <pre>{@code
 * // Correcto: la interface, no la implementación
 * import java.util.List;
 * import java.util.Iterator;
 *
 * public boolean equals(Object o) {
 *     if (!(o instanceof List<?> otra)) {   // List es una interface: permitido
 *         return false;
 *     }
 *     ...
 * }
 *
 * // Incorrecto: delegar en la implementación
 * // import java.util.ArrayList;
 * // ArrayList<String> copia = new ArrayList<>(otra);   // prohibido
 * }</pre>
 */
package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;
