package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comportamiento específico de {@link MyArrayList}: el backing array y su crecimiento.
 */
class MyArrayListTest {

	@Test
	@DisplayName("la capacidad inicial es 10 y crece ×1.5")
	void growsByHalf() {
		MyArrayList<String> list = new MyArrayList<>();

		assertThat(list.capacity()).isEqualTo(10);

		for (int i = 0; i < 10; i++) {
			list.add("v" + i);
		}
		assertThat(list.capacity()).isEqualTo(10);

		list.add("overflow");
		assertThat(list.capacity()).isEqualTo(15);

		for (int i = 0; i < 5; i++) {
			list.add("x" + i);
		}
		assertThat(list.capacity()).isEqualTo(22);
	}

	@Test
	@DisplayName("se puede pedir una capacidad inicial concreta")
	void honoursInitialCapacity() {
		MyArrayList<String> list = new MyArrayList<>(100);

		assertThat(list.capacity()).isEqualTo(100);
		assertThat(list.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("eliminar por posición deja la lista consistente")
	void removeByIndexKeepsListConsistent() {
		MyArrayList<String> list = new MyArrayList<>();
		list.add("a");
		list.add("b");

		list.remove(0);

		assertThat(list.size()).isEqualTo(1);
		assertThat(list.get(0)).isEqualTo("b");
	}

	@Test
	@DisplayName("insertar en medio desplaza los elementos siguientes")
	void insertInTheMiddleShifts() {
		MyArrayList<String> list = new MyArrayList<>();
		list.addAll(java.util.List.of("a", "c"));

		list.add(1, "b");

		assertThat(list).containsExactly("a", "b", "c");
	}

	@Test
	@DisplayName("contenido y tamaño sobreviven a muchas inserciones y borrados")
	void survivesChurn() {
		MyArrayList<Integer> list = new MyArrayList<>();
		Random random = new Random(42);

		for (int i = 0; i < 1_000; i++) {
			list.add(random.nextInt(100));
		}
		for (int i = 0; i < 500; i++) {
			list.remove(random.nextInt(list.size()));
		}

		assertThat(list.size()).isEqualTo(500);
		for (int i = 0; i < list.size(); i++) {
			assertThat(list.get(i)).isNotNull();
		}
	}
}
