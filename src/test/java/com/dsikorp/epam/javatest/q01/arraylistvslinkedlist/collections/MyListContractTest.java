package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Contrato común que deben cumplir {@link MyArrayList} y {@link MyLinkedList}.
 *
 * <p>El mismo juego de aserciones se ejecuta contra las dos implementaciones: es la forma más
 * directa de demostrar que ambas son intercambiables y de que el contrato de
 * {@link java.util.List} se cumple por igual.</p>
 */
class MyListContractTest {

	/**
	 * Una implementación bajo prueba, con nombre legible para los informes de test.
	 *
	 * @param name nombre de la implementación
	 * @param factory fábrica de listas nuevas
	 */
	record Implementation(String name, Supplier<MyList<Object>> factory) {

		MyList<Object> newList() {
			return factory.get();
		}

		@Override
		public String toString() {
			return name;
		}
	}

	static Stream<Implementation> implementations() {
		return Stream.of(new Implementation("MyArrayList", MyArrayList::new),
				new Implementation("MyLinkedList", MyLinkedList::new));
	}

	private static List<Object> seed() {
		return List.of("a", "b", "c");
	}

	// ------------------------------------------------------------------
	// Tamaño y contenido
	// ------------------------------------------------------------------

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("una lista nueva está vacía")
	void newListIsEmpty(Implementation impl) {
		MyList<Object> list = impl.newList();

		assertThat(list.size()).isZero();
		assertThat(list.isEmpty()).isTrue();
		assertThat(list.toArray()).isEmpty();
		assertThat(list.toString()).isEqualTo("[]");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("add al final devuelve true y crece la lista")
	void addAppends(Implementation impl) {
		MyList<Object> list = impl.newList();

		assertThat(list.add("a")).isTrue();
		assertThat(list.add("b")).isTrue();

		assertThat(list.size()).isEqualTo(2);
		assertThat(list.get(0)).isEqualTo("a");
		assertThat(list.get(1)).isEqualTo("b");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("get devuelve el elemento de la posición")
	void getReturnsElement(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		assertThat(list.get(0)).isEqualTo("a");
		assertThat(list.get(1)).isEqualTo("b");
		assertThat(list.get(2)).isEqualTo("c");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("get fuera de rango lanza IndexOutOfBoundsException")
	void getOutOfBoundsThrows(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		assertThatThrownBy(() -> list.get(3)).isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> list.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("set reemplaza y devuelve el valor anterior")
	void setReplacesAndReturnsOldValue(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		assertThat(list.set(1, "z")).isEqualTo("b");
		assertThat(list.get(1)).isEqualTo("z");
		assertThat(list.size()).isEqualTo(3);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("remove(int) elimina el elemento y desplaza los siguientes")
	void removeByIndexShifts(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		assertThat(list.remove(1)).isEqualTo("b");
		assertThat(list.size()).isEqualTo(2);
		assertThat(list.get(0)).isEqualTo("a");
		assertThat(list.get(1)).isEqualTo("c");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("remove(Object) elimina la primera ocurrencia")
	void removeByValueRemovesFirstOccurrence(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(List.of("a", "b", "a"));

		assertThat(list.remove("a")).isTrue();
		assertThat(list.size()).isEqualTo(2);
		assertThat(list.get(0)).isEqualTo("b");
		assertThat(list.get(1)).isEqualTo("a");
		assertThat(list.remove("no-existe")).isFalse();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("indexOf y lastIndexOf localizan las apariciones")
	void indexOfAndLastIndexOf(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(List.of("a", "b", "a"));

		assertThat(list.indexOf("a")).isZero();
		assertThat(list.lastIndexOf("a")).isEqualTo(2);
		assertThat(list.indexOf("b")).isEqualTo(1);
		assertThat(list.indexOf("zzz")).isEqualTo(-1);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("contains distingue entre null presente y null ausente")
	void containsHandlesNull(Implementation impl) {
		MyList<Object> list = impl.newList();

		assertThat(list.contains(null)).isFalse();
		list.add(null);
		assertThat(list.contains(null)).isTrue();
		assertThat(list.indexOf(null)).isZero();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("clear vacía la lista")
	void clearEmpties(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		list.clear();

		assertThat(list.size()).isZero();
		assertThat(list.isEmpty()).isTrue();
	}

	// ------------------------------------------------------------------
	// Inserción en posiciones
	// ------------------------------------------------------------------

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("add(index, e) inserta en cabeza, medio y cola")
	void addAtIndexPreservesOrder(Implementation impl) {
		MyList<Object> list = impl.newList();

		list.add(0, "b");
		list.add(1, "c");
		list.add(0, "a");
		list.add(3, "d");

		assertThat(list).containsExactly("a", "b", "c", "d");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("add(index, e) fuera de rango lanza IndexOutOfBoundsException")
	void addAtInvalidIndexThrows(Implementation impl) {
		MyList<Object> list = impl.newList();

		assertThatThrownBy(() -> list.add(1, "a")).isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> list.add(-1, "a")).isInstanceOf(IndexOutOfBoundsException.class);
	}

	// ------------------------------------------------------------------
	// Iteradores
	// ------------------------------------------------------------------

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("el iterador recorre en orden de inserción")
	void iteratorTraversesInOrder(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		StringBuilder seen = new StringBuilder();
		for (Object element : list) {
			seen.append(element);
		}

		assertThat(seen.toString()).isEqualTo("abc");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("iterator.remove borra el último elemento devuelto")
	void iteratorRemoveDeletesLastReturned(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		Iterator<Object> it = list.iterator();
		it.next();
		it.next();
		it.remove();

		assertThat(list).containsExactly("a", "c");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("el iterador es fail-fast ante modificaciones concurrentes")
	void iteratorIsFailFast(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		Iterator<Object> it = list.iterator();
		it.next();

		list.add("d");

		assertThatThrownBy(it::next).isInstanceOf(ConcurrentModificationException.class);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("el ListIterator va en ambos sentidos y puede fijar y añadir")
	void listIteratorIsBidirectional(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		ListIterator<Object> it = list.listIterator();
		assertThat(it.next()).isEqualTo("a");
		assertThat(it.previous()).isEqualTo("a");
		// Tras previous() el cursor vuelve a la posición 0.
		assertThat(it.nextIndex()).isZero();
		assertThat(it.previousIndex()).isEqualTo(-1);

		it.set("z");
		assertThat(list.get(0)).isEqualTo("z");

		// add() inserta justo antes de la posición del cursor.
		it.add("y");
		assertThat(list).containsExactly("y", "z", "b", "c");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("un iterador agotado lanza NoSuchElementException")
	void exhaustedIteratorThrows(Implementation impl) {
		MyList<Object> list = impl.newList();

		Iterator<Object> it = list.iterator();
		assertThatThrownBy(it::next).isInstanceOf(NoSuchElementException.class);
	}

	// ------------------------------------------------------------------
	// equals / hashCode / toString / toArray
	// ------------------------------------------------------------------

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("equals compara contra cualquier List, no sólo contra la misma implementación")
	void equalsIsCrossImplementation(Implementation impl) {
		MyList<Object> mine = impl.newList();
		mine.addAll(seed());

		MyList<Object> other = impl.newList();
		other.addAll(seed());

		assertThat(mine).isEqualTo(other);
		assertThat(mine.hashCode()).isEqualTo(other.hashCode());
		assertThat(mine).isNotEqualTo(List.of("a", "b"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("toString usa el formato [a, b, c] del JDK")
	void toStringMatchesJdkFormat(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		assertThat(list.toString()).isEqualTo("[a, b, c]");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("toArray devuelve una copia con el mismo contenido")
	void toArrayCopiesContent(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		Object[] array = list.toArray();

		assertThat(array).containsExactly("a", "b", "c");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("toArray(T[]) reutiliza el array cuando hay sitio")
	void toArrayWithTargetReuses(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		Object[] target = new Object[5];
		Object[] result = list.toArray(target);

		assertThat(result).isSameAs(target);
		assertThat(result[0]).isEqualTo("a");
		assertThat(result[3]).isNull();
	}

	// ------------------------------------------------------------------
	// Operaciones de conjunto
	// ------------------------------------------------------------------

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("addAll, containsAll, removeAll y retainAll")
	void bulkOperations(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		assertThat(list.addAll(List.of("d", "e"))).isTrue();
		assertThat(list).containsExactly("a", "b", "c", "d", "e");
		assertThat(list.containsAll(List.of("a", "c"))).isTrue();
		assertThat(list.containsAll(List.of("a", "zzz"))).isFalse();

		assertThat(list.removeAll(List.of("b", "d"))).isTrue();
		assertThat(list).containsExactly("a", "c", "e");

		assertThat(list.retainAll(List.of("a", "e"))).isTrue();
		assertThat(list).containsExactly("a", "e");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("sort ordena con el comparador indicado")
	void sortUsesComparator(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(List.of("c", "a", "b"));

		list.sort((left, right) -> ((String) left).compareTo((String) right));

		assertThat(list).containsExactly("a", "b", "c");
	}

	// ------------------------------------------------------------------
	// Crecimiento y vistas
	// ------------------------------------------------------------------

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("la lista crece sin perder elementos")
	void growsBeyondInitialCapacity(Implementation impl) {
		MyList<Object> list = impl.newList();

		for (int i = 0; i < 1_000; i++) {
			list.add(i);
		}

		assertThat(list.size()).isEqualTo(1_000);
		assertThat(list.get(0)).isEqualTo(0);
		assertThat(list.get(999)).isEqualTo(999);
		assertThat(list.indexOf(500)).isEqualTo(500);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("implementations")
	@DisplayName("subList es una vista: lo que se muta en ella se ve en el padre")
	void subListIsAView(Implementation impl) {
		MyList<Object> list = impl.newList();
		list.addAll(seed());

		List<Object> view = list.subList(1, 3);
		view.set(0, "z");

		assertThat(list.get(1)).isEqualTo("z");
		assertThat(view).containsExactly("z", "c");
	}
}
