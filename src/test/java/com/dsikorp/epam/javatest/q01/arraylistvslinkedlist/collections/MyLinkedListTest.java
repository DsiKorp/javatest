package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comportamiento específico de {@link MyLinkedList}: la cadena de nodos y la API de
 * {@link java.util.Deque}.
 */
class MyLinkedListTest {

	@Test
	@DisplayName("addFirst y addLast mantienen coherentes los dos extremos")
	void keepsBothEndsCoherent() {
		MyLinkedList<String> list = new MyLinkedList<>();

		list.addLast("b");
		list.addFirst("a");
		list.addLast("c");

		assertThat(list.getFirst()).isEqualTo("a");
		assertThat(list.getLast()).isEqualTo("c");
		assertThat(list).containsExactly("a", "b", "c");
		assertThat(list.size()).isEqualTo(3);
	}

	@Test
	@DisplayName("removeFirst y removeLast rehacen los enlaces")
	void removeFromBothEnds() {
		MyLinkedList<String> list = new MyLinkedList<>();
		list.addAll(java.util.List.of("a", "b", "c"));

		assertThat(list.removeFirst()).isEqualTo("a");
		assertThat(list.removeLast()).isEqualTo("c");

		assertThat(list).containsExactly("b");
		assertThat(list.getFirst()).isEqualTo("b");
		assertThat(list.getLast()).isEqualTo("b");
	}

	@Test
	@DisplayName("los extremos vacíos lanzan NoSuchElementException")
	void emptyEndsThrow() {
		MyLinkedList<String> list = new MyLinkedList<>();

		assertThatThrownBy(list::getFirst).isInstanceOf(NoSuchElementException.class);
		assertThatThrownBy(list::getLast).isInstanceOf(NoSuchElementException.class);
		assertThatThrownBy(list::removeFirst).isInstanceOf(NoSuchElementException.class);
		assertThatThrownBy(list::removeLast).isInstanceOf(NoSuchElementException.class);
		assertThatThrownBy(list::pop).isInstanceOf(NoSuchElementException.class);
		assertThatThrownBy(list::element).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	@DisplayName("se comporta como una cola FIFO")
	void worksAsFifoQueue() {
		MyLinkedList<String> list = new MyLinkedList<>();

		list.offer("primero");
		list.offer("segundo");

		assertThat(list.peek()).isEqualTo("primero");
		assertThat(list.poll()).isEqualTo("primero");
		assertThat(list.poll()).isEqualTo("segundo");
		assertThat(list.poll()).isNull();
	}

	@Test
	@DisplayName("se comporta como una pila LIFO")
	void worksAsLifoStack() {
		MyLinkedList<String> list = new MyLinkedList<>();

		list.push("a");
		list.push("b");

		assertThat(list.pop()).isEqualTo("b");
		assertThat(list).containsExactly("a");
	}

	@Test
	@DisplayName("el iterador hacia atrás recorre la lista al revés")
	void listIteratorWalksBackwards() {
		MyLinkedList<String> list = new MyLinkedList<>();
		list.addAll(java.util.List.of("a", "b", "c"));

		var iterator = list.listIterator(list.size());
		assertThat(iterator.hasPrevious()).isTrue();
		assertThat(iterator.previous()).isEqualTo("c");
		assertThat(iterator.previous()).isEqualTo("b");
		assertThat(iterator.previous()).isEqualTo("a");
		assertThat(iterator.hasPrevious()).isFalse();
	}

	@Test
	@DisplayName("no es RandomAccess: el acceso posicional es lineal")
	void isNotRandomAccess() {
		assertThat(new MyLinkedList<String>()).isNotInstanceOf(java.util.RandomAccess.class);
		assertThat(new MyArrayList<String>()).isInstanceOf(java.util.RandomAccess.class);
	}

	@Test
	@DisplayName("insertar en medio enlaza el nodo nuevo con sus dos vecinos")
	void insertsInTheMiddle() {
		MyLinkedList<String> list = new MyLinkedList<>();
		list.addAll(java.util.List.of("a", "c"));

		list.add(1, "b");

		assertThat(list).containsExactly("a", "b", "c");
		assertThat(list.getFirst()).isEqualTo("a");
		assertThat(list.getLast()).isEqualTo("c");
	}
}
