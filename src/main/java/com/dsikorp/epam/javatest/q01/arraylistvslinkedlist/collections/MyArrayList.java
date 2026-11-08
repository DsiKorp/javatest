package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import java.util.Collection;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;

/**
 * Lista respaldada por un array redimensionable, implementada desde cero.
 *
 * <p>Reproduce el diseño de {@code java.util.ArrayList}:</p>
 * <ul>
 *   <li>Los elementos se guardan en un {@code Object[]} (array crudo, por los genéricos) y
 *       {@code size} es el número de posiciones ocupadas.</li>
 *   <li>Al insertar en medio se desplaza el bloque con {@code System.arraycopy}: coste O(n).</li>
 *   <li>La capacidad crece ×1.5 ({@code old + (old >> 1)}), como en el JDK.</li>
 *   <li>Acceso posicional O(1); implementación de {@link RandomAccess} para que los bucles
 *       optimizados de Java distingan esta lista de una enlazada.</li>
 * </ul>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * MyArrayList<String> lista = new MyArrayList<>();
 * lista.add("a");
 * lista.add("c");
 * lista.add(1, "b");              // inserta en medio: ["a", "b", "c"]
 *
 * String segundo = lista.get(1);  // "b"
 * lista.set(1, "z");              // devuelve el valor anterior: "b"
 * lista.remove(0);                // devuelve "a"  →  ["z", "c"]
 *
 * System.out.println(lista);        // [z, c]
 * System.out.println(lista.size()); // 2
 * System.out.println(lista.indexOf("c")); // 1
 *
 * for (String elemento : lista) {
 *     System.out.println(elemento);  // z, c
 * }
 * }</pre>
 *
 * <p>El contrato completo de {@link java.util.List} lo aporta {@link AbstractMyList}: esta clase
 * sólo implementa las seis primitivas.</p>
 *
 * @param <E> tipo de los elementos
 */
public class MyArrayList<E> extends AbstractMyList<E> implements RandomAccess {

	private static final int DEFAULT_CAPACITY = 10;

	/** Tope práctico del tamaño de un array en la JVM. */
	private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

	/** Array de almacenamiento. Sólo las posiciones {@code [0, size)} están ocupadas. */
	private Object[] elementData;

	/** Número de elementos almacenados. */
	private int size;

	/**
	 * Crea una lista vacía con la capacidad inicial por defecto.
	 */
	public MyArrayList() {
		this.elementData = new Object[DEFAULT_CAPACITY];
	}

	/**
	 * Crea una lista vacía con la capacidad inicial indicada.
	 *
	 * @param initialCapacity capacidad inicial
	 * @throws IllegalArgumentException si la capacidad inicial es negativa
	 */
	public MyArrayList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
		this.elementData = new Object[initialCapacity];
	}

	/**
	 * Crea una lista con los elementos de la colección dada.
	 *
	 * @param collection colección de origen
	 */
	public MyArrayList(Collection<? extends E> collection) {
		this.elementData = new Object[Math.max(collection.size(), DEFAULT_CAPACITY)];
		for (E element : collection) {
			add(element);
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E get(int index) {
		checkIndex(index);
		return (E) elementData[index];
	}

	@Override
	@SuppressWarnings("unchecked")
	public E set(int index, E element) {
		checkIndex(index);
		E previous = (E) elementData[index];
		elementData[index] = element;
		return previous;
	}

	@Override
	public void add(int index, E element) {
		checkPositionIndex(index);
		ensureCapacity(size + 1);
		int moved = size - index;
		if (moved > 0) {
			System.arraycopy(elementData, index, elementData, index + 1, moved);
		}
		elementData[index] = element;
		size++;
		modCount++;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E remove(int index) {
		checkIndex(index);
		E removed = (E) elementData[index];
		int moved = size - index - 1;
		if (moved > 0) {
			System.arraycopy(elementData, index + 1, elementData, index, moved);
		}
		elementData[--size] = null;
		modCount++;
		return removed;
	}

	@Override
	public void clear() {
		for (int i = 0; i < size; i++) {
			elementData[i] = null;
		}
		size = 0;
		modCount++;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index);
		return new MyListIterator<>(this, index);
	}

	@Override
	public int indexOf(Object o) {
		for (int i = 0; i < size; i++) {
			if (Objects.equals(o, elementData[i])) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = size - 1; i >= 0; i--) {
			if (Objects.equals(o, elementData[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Devuelve una lista con los mismos elementos en orden inverso.
	 *
	 * <p><strong>Desviación consciente respecto al JDK</strong>: {@code java.util.List.reversed()}
	 * devuelve una <em>vista</em> en la que las mutaciones se escriben en la lista original;
	 * aquí se devuelve una <em>copia</em>. El motivo es que {@code java.util.Deque.reversed()}
	 * (que {@link MyLinkedList} necesita por implementar {@code Deque}) tiene la misma firma con
	 * otro tipo de retorno, y una vista invertida coherente para ambas estructuras exigiría
	 * duplicar toda la maquinaria de delegación. El comportamiento es idéntico en
	 * {@code MyArrayList} y en {@code MyLinkedList}.</p>
	 *
	 * @return una lista nueva con los elementos en orden inverso
	 */
	@Override
	public MyArrayList<E> reversed() {
		MyArrayList<E> copy = new MyArrayList<>(size());
		for (int i = size() - 1; i >= 0; i--) {
			copy.add(get(i));
		}
		return copy;
	}

	/**
	 * @return la capacidad actual del array de almacenamiento
	 */
	int capacity() {
		return elementData.length;
	}

	private void ensureCapacity(int minCapacity) {
		if (minCapacity > elementData.length) {
			grow(minCapacity);
		}
	}

	private void grow(int minCapacity) {
		if (minCapacity > MAX_CAPACITY) {
			throw new OutOfMemoryError("Required array size too large: " + minCapacity);
		}
		int oldCapacity = elementData.length;
		// Crecimiento ×1.5, el mismo que usa java.util.ArrayList.
		int newCapacity = Math.max(oldCapacity + (oldCapacity >> 1), minCapacity);
		if (newCapacity == 0) {
			newCapacity = 1;
		}
		if (newCapacity > MAX_CAPACITY) {
			newCapacity = MAX_CAPACITY;
		}
		Object[] grown = new Object[newCapacity];
		System.arraycopy(elementData, 0, grown, 0, size);
		elementData = grown;
	}
}
