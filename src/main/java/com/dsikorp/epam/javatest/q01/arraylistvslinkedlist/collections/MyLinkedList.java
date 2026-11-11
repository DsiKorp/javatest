package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista respaldada por una cadena de nodos doblemente enlazados, implementada desde cero.
 *
 * <p>Reproduce el diseño de {@code java.util.LinkedList}, que además implementa
 * {@link Deque} y por tanto puede usarse como cola FIFO:</p>
 * <ul>
 * <li>Cada nodo guarda su elemento y sus dos vecinos ({@code next} y {@code prev}), lo que
 * permite recorrer la lista en ambos sentidos.</li>
 * <li>Insertar y quitar en los extremos es O(1): basta con rehacer dos enlaces.</li>
 * <li>Insertar o quitar por posición es O(n): hay que llegar hasta el nodo, y para eso se
 * recorre desde el extremo más cercano.</li>
 * <li>A diferencia de {@link MyArrayList}, no es {@link java.util.RandomAccess}: el acceso
 * posicional es lineal.</li>
 * </ul>
 *
 * <p><strong>Ejemplo como lista:</strong></p>
 * <pre>{@code
 * MyLinkedList<String> lista = new MyLinkedList<>();
 * lista.addLast("b");
 * lista.addFirst("a");            // ["a", "b"]
 * lista.add(1, "c");              // ["a", "c", "b"]
 *
 * System.out.println(lista.getFirst());  // a
 * System.out.println(lista.getLast());   // b
 * System.out.println(lista.get(1));      // c
 * }</pre>
 *
 * <p><strong>Ejemplo como cola FIFO</strong> (de ahí que implemente {@link Deque}):</p>
 * <pre>{@code
 * MyLinkedList<String> cola = new MyLinkedList<>();
 * cola.offer("primero");          // offer == addLast
 * cola.offer("segundo");
 *
 * System.out.println(cola.peek());  // primero   (no consume)
 * System.out.println(cola.poll());  // primero   (consume la cabeza)
 * System.out.println(cola.poll());  // segundo
 * System.out.println(cola.poll());  // null      (la cola ya está vacía)
 * }</pre>
 *
 * <p><strong>Ejemplo como pila LIFO:</strong></p>
 * <pre>{@code
 * MyLinkedList<String> pila = new MyLinkedList<>();
 * pila.push("a");
 * pila.push("b");                  // ["b", "a"]
 *
 * System.out.println(pila.pop());  // b
 * }</pre>
 *
 * @param <E> tipo de los elementos
 */
public class MyLinkedList<E> extends AbstractMyList<E> implements Deque<E> {

	/** Nodo de la cadena. */
	private static final class Node<E> {

		private E element;

		private Node<E> next;

		private Node<E> prev;

		Node(E element) {
			this.element = element;
		}
	}

	/** Primer nodo de la lista, o {@code null} si está vacía. */
	private Node<E> first;

	/** Último nodo de la lista, o {@code null} si está vacía. */
	private Node<E> last;

	/** Número de nodos. */
	private int size;

	/**
  * Crea una lista vacía.
  */
	public MyLinkedList() {
	}

	/**
  * Crea una lista con los elementos de la colección dada.
  *
  * @param collection colección de origen
  */
	public MyLinkedList(Collection<? extends E> collection) {
		for (E element : collection) {
			add(element);
		}
	}

	// ------------------------------------------------------------------
	// Primitivas de List
	// ------------------------------------------------------------------

	@Override
	public int size() {
		return size;
	}

	@Override
	public E get(int index) {
		return node(index).element;
	}

	@Override
	public E set(int index, E element) {
		Node<E> target = node(index);
		E previous = target.element;
		target.element = element;
		return previous;
	}

	@Override
	public void add(int index, E element) {
		checkPositionIndex(index);
		if (index == 0) {
			addFirst(element);
		}
		else if (index == size) {
			addLast(element);
		}
		else {
			linkAfter(node(index - 1), element);
			size++;
			modCount++;
		}
	}

	@Override
	public E remove(int index) {
		checkIndex(index);
		return unlink(node(index));
	}

	@Override
	public void clear() {
		Node<E> current = first;
		while (current != null) {
			Node<E> next = current.next;
			current.element = null;
			current.next = null;
			current.prev = null;
			current = next;
		}
		first = null;
		last = null;
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
		int index = 0;
		for (Node<E> current = first; current != null; current = current.next, index++) {
			if (Objects.equals(o, current.element)) {
				return index;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int index = size - 1;
		for (Node<E> current = last; current != null; current = current.prev, index--) {
			if (Objects.equals(o, current.element)) {
				return index;
			}
		}
		return -1;
	}

	@Override
	public boolean contains(Object o) {
		for (Node<E> current = first; current != null; current = current.next) {
			if (Objects.equals(o, current.element)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		for (Node<E> current = first; current != null; current = current.next) {
			if (Objects.equals(o, current.element)) {
				unlink(current);
				return true;
			}
		}
		return false;
	}

	// ------------------------------------------------------------------
	// API de Deque
	// ------------------------------------------------------------------

	@Override
	public void addFirst(E element) {
		linkFirst(element);
		size++;
		modCount++;
	}

	@Override
	public void addLast(E element) {
		linkLast(element);
		size++;
		modCount++;
	}

	@Override
	public E removeFirst() {
		if (first == null) {
			throw new NoSuchElementException();
		}
		return unlink(first);
	}

	@Override
	public E removeLast() {
		if (last == null) {
			throw new NoSuchElementException();
		}
		return unlink(last);
	}

	@Override
	public E getFirst() {
		if (first == null) {
			throw new NoSuchElementException();
		}
		return first.element;
	}

	@Override
	public E getLast() {
		if (last == null) {
			throw new NoSuchElementException();
		}
		return last.element;
	}

	@Override
	public boolean offerFirst(E element) {
		addFirst(element);
		return true;
	}

	@Override
	public boolean offerLast(E element) {
		addLast(element);
		return true;
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		for (Node<E> current = first; current != null; current = current.next) {
			if (Objects.equals(o, current.element)) {
				unlink(current);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		for (Node<E> current = last; current != null; current = current.prev) {
			if (Objects.equals(o, current.element)) {
				unlink(current);
				return true;
			}
		}
		return false;
	}

	@Override
	public E pollFirst() {
		return first == null ? null : removeFirst();
	}

	@Override
	public E pollLast() {
		return last == null ? null : removeLast();
	}

	@Override
	public E peekFirst() {
		return first == null ? null : first.element;
	}

	@Override
	public E peekLast() {
		return last == null ? null : last.element;
	}

	@Override
	public void push(E element) {
		addFirst(element);
	}

	@Override
	public E pop() {
		return removeFirst();
	}

	@Override
	public E element() {
		return getFirst();
	}

	@Override
	public boolean offer(E element) {
		return add(element);
	}

	@Override
	public E poll() {
		return pollFirst();
	}

	@Override
	public E peek() {
		return peekFirst();
	}

	/**
  * Quita y devuelve la cabeza de la lista.
  *
  * @return la cabeza, o {@code null} si la lista está vacía
  */
	@Override
	public E remove() {
		return removeFirst();
	}

	@Override
	public Iterator<E> descendingIterator() {
		ListIterator<E> delegate = listIterator(size);
		return new Iterator<>() {

	@Override
			public boolean hasNext() {
				return delegate.hasPrevious();
			}

	@Override
			public E next() {
				return delegate.previous();
			}
		};
	}

	/**
  * Devuelve una lista con los mismos elementos en orden inverso.
  *
  * <p><strong>Desviación consciente respecto al JDK</strong>: el JDK devuelve una
  * <em>vista</em> invertida; aquí se devuelve una <em>copia</em>. El motivo es que
  * {@code java.util.List.reversed()} y {@code java.util.Deque.reversed()} tienen la misma
  * firma con tipos de retorno distintos, y esta clase implementa las dos interfaces. El
  * comportamiento es idéntico en {@code MyArrayList} y en {@code MyLinkedList}.</p>
  *
  * @return una lista nueva con los elementos en orden inverso
  */
	@Override
	public MyLinkedList<E> reversed() {
		MyLinkedList<E> copy = new MyLinkedList<>();
		for (Node<E> current = last; current != null; current = current.prev) {
			copy.addLast(current.element);
		}
		return copy;
	}

	// ------------------------------------------------------------------
	// Internals
	// ------------------------------------------------------------------

	/**
  * Devuelve el nodo que ocupa la posición indicada, caminando desde el extremo más cercano.
  *
  * @param index posición del nodo
  * @return el nodo
  */
	private Node<E> node(int index) {
		checkIndex(index);
		Node<E> current;
		if (index < (size >> 1)) {
			current = first;
			for (int i = 0; i < index; i++) {
				current = current.next;
			}
		}
		else {
			current = last;
			for (int i = size - 1; i > index; i--) {
				current = current.prev;
			}
		}
		return current;
	}

	/**
  * Inserta un nodo nuevo al principio de la lista.
  */
	private void linkFirst(E element) {
		Node<E> created = new Node<>(element);
		if (first == null) {
			first = created;
			last = created;
		}
		else {
			created.next = first;
			first.prev = created;
			first = created;
		}
	}

	/**
  * Inserta un nodo nuevo al final de la lista.
  */
	private void linkLast(E element) {
		Node<E> created = new Node<>(element);
		if (last == null) {
			first = created;
			last = created;
		}
		else {
			created.prev = last;
			last.next = created;
			last = created;
		}
	}

	/**
  * Inserta un nodo nuevo justo después del nodo dado, que no puede ser {@code null}.
  */
	private void linkAfter(Node<E> node, E element) {
		Node<E> created = new Node<>(element);
		created.prev = node;
		created.next = node.next;
		node.next = created;
		if (created.next == null) {
			last = created;
		}
		else {
			created.next.prev = created;
		}
	}

	/**
  * Desconecta un nodo de la cadena y devuelve su elemento.
  */
	private E unlink(Node<E> node) {
		E element = node.element;
		Node<E> previous = node.prev;
		Node<E> next = node.next;
		if (previous == null) {
			first = next;
		}
		else {
			previous.next = next;
		}
		if (next == null) {
			last = previous;
		}
		else {
			next.prev = previous;
		}
		node.element = null;
		node.next = null;
		node.prev = null;
		size--;
		modCount++;
		return element;
	}
}
