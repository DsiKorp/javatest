package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Compara, paso a paso, el comportamiento de las implementaciones propias con el de las del JDK.
 *
 * <p>Se aplica la misma secuencia determinista de operaciones sobre {@link MyArrayList} y
 * {@code java.util.ArrayList} (y sobre {@link MyLinkedList} y {@code java.util.LinkedList}), y
 * después de cada operación se compara el estado completo de ambas listas. Es la evidencia más
 * fuerte que se puede dar de que las implementaciones propias respetan el contrato: no "se
 * parecen" al JDK, es que se comportan como el JDK.</p>
 *
 * <p>El uso de {@code java.util.ArrayList} y {@code java.util.LinkedList} aquí es deliberado y
 * no viola la regla del paquete {@code collections}: esa regla aplica a las <em>implementaciones</em>,
 * no a los tests, y usar el JDK como oráculo es precisamente lo que da valor a esta prueba.</p>
 */
class MyListMatchesJdkTest {

	private static final int SEED = 20241107;

	private static final int OPERATIONS = 500;

	static Stream<Arguments> pairs() {
		return Stream.of(
				Arguments.of("MyArrayList vs ArrayList", (Supplier<MyList<Object>>) MyArrayList::new,
						(Supplier<List<Object>>) java.util.ArrayList::new),
				Arguments.of("MyLinkedList vs LinkedList", (Supplier<MyList<Object>>) MyLinkedList::new,
						(Supplier<List<Object>>) LinkedList::new));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("pairs")
	@DisplayName("el comportamiento coincide con el del JDK, operación a operación")
	void behavesLikeTheJdk(String name, Supplier<MyList<Object>> mineFactory,
			Supplier<List<Object>> jdkFactory) {
		MyList<Object> mine = mineFactory.get();
		List<Object> jdk = jdkFactory.get();
		Random random = new Random(SEED);

		for (int step = 0; step < OPERATIONS; step++) {
			int operation = random.nextInt(7);
			int index = random.nextInt(6);
			Object value = "v" + random.nextInt(4);

			switch (operation) {
				case 0 -> {
					mine.add(value);
					jdk.add(value);
				}
				case 1 -> {
					if (!jdk.isEmpty()) {
						int position = random.nextInt(jdk.size());
						assertThat(mine.get(position)).isEqualTo(jdk.get(position));
					}
				}
				case 2 -> {
					if (!jdk.isEmpty()) {
						int position = random.nextInt(jdk.size());
						assertThat(mine.set(position, value)).isEqualTo(jdk.set(position, value));
					}
				}
				case 3 -> {
					if (!jdk.isEmpty()) {
						int position = random.nextInt(jdk.size());
						assertThat(mine.remove(position)).isEqualTo(jdk.remove(position));
					}
				}
				case 4 -> {
					assertThat(mine.indexOf(value)).isEqualTo(jdk.indexOf(value));
					assertThat(mine.lastIndexOf(value)).isEqualTo(jdk.lastIndexOf(value));
					assertThat(mine.contains(value)).isEqualTo(jdk.contains(value));
				}
				case 5 -> {
					if (jdk.size() > 1) {
						int position = random.nextInt(jdk.size() - 1) + 1;
						mine.add(position, value);
						jdk.add(position, value);
					}
				}
				default -> {
					if (!jdk.isEmpty()) {
						assertThat(mine.remove(value)).isEqualTo(jdk.remove(value));
					}
				}
			}

			assertThat(mine.size()).as("tamaño tras la operación %d", step).isEqualTo(jdk.size());
			assertThat(mine.toString()).as("contenido tras la operación %d", step).isEqualTo(jdk.toString());
			assertThat(mine.toArray()).as("array tras la operación %d", step).isEqualTo(jdk.toArray());
			assertThat(mine.hashCode()).as("hash tras la operación %d", step).isEqualTo(jdk.hashCode());
			assertThat(mine).as("igualdad tras la operación %d", step).isEqualTo(jdk);
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("pairs")
	@DisplayName("el iterador coincide con el del JDK")
	void iteratorMatchesTheJdk(String name, Supplier<MyList<Object>> mineFactory,
			Supplier<List<Object>> jdkFactory) {
		MyList<Object> mine = mineFactory.get();
		List<Object> jdk = jdkFactory.get();
		Random random = new Random(SEED);

		for (int i = 0; i < 100; i++) {
			String value = "v" + random.nextInt(5);
			mine.add(value);
			jdk.add(value);
		}

		assertThat(mine).isEqualTo(jdk);

		var mineIterator = mine.listIterator();
		var jdkIterator = jdk.listIterator();
		while (jdkIterator.hasNext()) {
			assertThat(mineIterator.next()).isEqualTo(jdkIterator.next());
			assertThat(mineIterator.nextIndex()).isEqualTo(jdkIterator.nextIndex());
			assertThat(mineIterator.previousIndex()).isEqualTo(jdkIterator.previousIndex());
		}
	}
}
