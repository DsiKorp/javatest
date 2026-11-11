package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Iterador bidireccional compartido por {@link MyArrayList} y {@link MyLinkedList}.
 *
 * <p>Reproduce el comportamiento del JDK:</p>
 * <ul>
 * <li><strong>Fail-fast</strong>: si la lista se modifica estructuralmente mientras se recorre,
 * la siguiente llamada a {@link #next()}, {@link #previous()}, {@link #set(Object)},
 * {@link #add(Object)} o {@link #remove()} lanza {@link ConcurrentModificationException}.</li>
 * <li>{@link #remove()} borra el último elemento devuelto y no puede invocarse dos veces
 * seguidas sin una llamada intermedia a {@code next}/{@code previous}.</li>
 * <li>{@link #add(Object)} inserta en la posición del cursor y deja el cursor detrás del
 * elemento insertado.</li>
 * </ul>
 *
 * <p><strong>Ejemplo: recorrido y borrado durante el recorrido.</strong></p>
 * <pre>{@code
 * MyArrayList<String> lista = new MyArrayList<>();
 * lista.addAll(List.of("a", "b", "c"));
 *
 * Iterator<String> iterador = lista.iterator();
 * while (iterador.hasNext()) {
 * String elemento = iterador.next();
 * if (elemento.equals("b")) {
 * iterador.remove();      // borra el último devuelto: ["a", "c"]
 * }
 * }
 * }</pre>
 *
 * <p><strong>Ejemplo: iteración bidireccional.</strong> Un {@code ListIterator} conoce la
 * posición del cursor, así que puede ir hacia atrás:</p>
 * <pre>{@code
 * ListIterator<String> iterador = lista.listIterator(lista.size());
 *
 * iterador.hasPrevious();   // true
 * iterador.previous();      // "c"   (el cursor retrocede)
 * iterador.nextIndex();     // 2
 * iterador.previousIndex(); // 1
 * }</pre>
 *
 * <p><strong>Ejemplo: fail-fast.</strong> Si la lista se modifica estructuralmente mientras se
 * recorre, el iterador lo detecta:</p>
 * <pre>{@code
 * Iterator<String> iterador = lista.iterator();
 * iterador.next();
 * lista.add("d");           // modificación estructural
 *
 * iterador.next();          // lanza ConcurrentModificationException
 * }</pre>
 *
 * @param <E> tipo de los elementos
 */
final class MyListIterator<E> implements ListIterator<E> {

	private final AbstractMyList<E> list;

	/** Valor de {@code modCount} al crear el iterador; si cambia, hay modificación concurrente. */
	private int expectedModCount;

	/** Índice del siguiente elemento a devolver. */
	private int cursor;

	/** Índice del último elemento devuelto por {@code next}/{@code previous}, o {@code -1}. */
	private int lastRet = -1;

	MyListIterator(AbstractMyList<E> list, int index) {
		this.list = list;
		this.expectedModCount = list.modCount();
		this.cursor = index;
	}

	@Override
	public boolean hasNext() {
		return cursor != list.size();
	}

	@Override
	public E next() {
		checkForComodification();
		if (cursor >= list.size()) {
			throw new NoSuchElementException();
		}
		lastRet = cursor;
		E next = list.get(cursor);
		cursor++;
		return next;
	}

	@Override
	public boolean hasPrevious() {
		return cursor > 0;
	}

	@Override
	public E previous() {
		checkForComodification();
		if (cursor <= 0) {
			throw new NoSuchElementException();
		}
		cursor--;
		lastRet = cursor;
		return list.get(cursor);
	}

	@Override
	public int nextIndex() {
		return cursor;
	}

	@Override
	public int previousIndex() {
		return cursor - 1;
	}

	@Override
	public void remove() {
		checkForComodification();
		if (lastRet < 0) {
			throw new IllegalStateException();
		}
		list.remove(lastRet);
		if (lastRet < cursor) {
			cursor--;
		}
		lastRet = -1;
		expectedModCount = list.modCount();
	}

	@Override
	public void set(E element) {
		checkForComodification();
		if (lastRet < 0) {
			throw new IllegalStateException();
		}
		list.set(lastRet, element);
	}

	@Override
	public void add(E element) {
		checkForComodification();
		list.add(cursor, element);
		lastRet = -1;
		cursor++;
		expectedModCount = list.modCount();
	}

	private void checkForComodification() {
		if (list.modCount() != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}
}
