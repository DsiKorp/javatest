package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.verification;

import static com.dsikorp.epam.javatest.common.verification.Verifications.equal;
import static com.dsikorp.epam.javatest.common.verification.Verifications.isTrue;
import static com.dsikorp.epam.javatest.common.verification.Verifications.throwsException;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.dsikorp.epam.javatest.common.verification.Check;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyLinkedList;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyList;

/**
 * Batería de comprobaciones del contrato de {@link java.util.List}.
 *
 * <p>Las comprobaciones de {@link #common(ListType)} se ejecutan contra <em>las dos</em>
 * implementaciones, lo que demuestra que ambas cumplen el mismo contrato. Las de
 * {@link #dequeOnly(ListType)} sólo se aplican a {@link com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyLinkedList},
 * que además implementa {@link java.util.Deque}.</p>
 *
 * <p>Esta clase es la que se expone por HTTP: es el "ejercicio" que el entrevistador puede
 * ejecutar contra la implementación sin necesidad de leer el código.</p>
 *
 * <p><strong>Ejemplo: cómo se escribe una comprobación.</strong> Recibe una lista vacía de la
 * implementación que se está probando y debe dejar la lista en el estado que necesite. Si la
 * comprobación falla, lanza {@link AssertionError} con el motivo:</p>
 * <pre>{@code
 * check(type, "remove(Object) elimina la primera ocurrencia", (list, impl) -> {
 * list.addAll(List.of("a", "b", "a"));
 *
 * isTrue(list.remove("a"), "remove devuelve true");
 * equal(2, list.size(), "size tras remove");
 * equal("b", list.get(0), "se elimina la primera ocurrencia");
 * equal("a", list.get(1), "la segunda ocurrencia se conserva");
 *
 * isTrue(!list.remove("no-existe"), "remove de un valor ausente devuelve false");
 * });
 * }</pre>
 *
 * <p>Las comprobaciones se declaran en dos bloques: {@link #common(ListType)} se ejecuta contra las dos
 * implementaciones, y {@link #dequeOnly(ListType)} sólo contra las que también implementan
 * {@code Deque}.</p>
 */
public final class ListContractChecks {

	private ListContractChecks() {
	}

	/**
  * Las comprobaciones del contrato que deben superarse en cualquier implementación de lista.
  *
  * @return las comprobaciones que deben superarse en cualquier implementación
  * @param type implementación sobre la que se ejecuta
  */
	public static List<Check> common(ListType type) {
		return List.of(
				check(type, "una lista nueva está vacía", (list, impl) -> {
					equal(0, list.size(), "size");
					isTrue(list.isEmpty(), "isEmpty");
					equal("[]", list.toString(), "toString");
				}),
				check(type, "add al final devuelve true y conserva el orden", (list, impl) -> {
					isTrue(list.add("a"), "add devuelve true");
					isTrue(list.add("b"), "add devuelve true");
					equal(2, list.size(), "size");
					equal("a", list.get(0), "get(0)");
					equal("b", list.get(1), "get(1)");
				}),
				check(type, "get fuera de rango lanza IndexOutOfBoundsException", (list, impl) -> {
					list.add("a");
					throwsException(IndexOutOfBoundsException.class, () -> list.get(1),
							"get con índice fuera de rango");
					throwsException(IndexOutOfBoundsException.class, () -> list.get(-1),
							"get con índice negativo");
				}),
				check(type, "set reemplaza y devuelve el valor anterior", (list, impl) -> {
					list.addAll(List.of("a", "b"));
					equal("a", list.set(0, "z"), "set devuelve el valor anterior");
					equal("z", list.get(0), "el elemento ha cambiado");
					equal(2, list.size(), "set no cambia el tamaño");
				}),
				check(type, "remove(int) elimina el elemento y desplaza los siguientes", (list, impl) -> {
					list.addAll(List.of("a", "b", "c"));
					equal("b", list.remove(1), "remove devuelve el elemento eliminado");
					equal(2, list.size(), "size tras remove");
					equal("a", list.get(0), "el primer elemento no se mueve");
					equal("c", list.get(1), "los elementos posteriores se desplazan");
				}),
				check(type, "remove(Object) elimina la primera ocurrencia", (list, impl) -> {
					list.addAll(List.of("a", "b", "a"));
					isTrue(list.remove("a"), "remove devuelve true");
					equal(2, list.size(), "size tras remove");
					equal("b", list.get(0), "se elimina la primera ocurrencia");
					equal("a", list.get(1), "la segunda ocurrencia se conserva");
					isTrue(!list.remove("no-existe"), "remove de un valor ausente devuelve false");
				}),
				check(type, "indexOf y lastIndexOf localizan las apariciones", (list, impl) -> {
					list.addAll(List.of("a", "b", "a"));
					equal(0, list.indexOf("a"), "indexOf");
					equal(2, list.lastIndexOf("a"), "lastIndexOf");
					equal(1, list.indexOf("b"), "indexOf");
					equal(-1, list.indexOf("zzz"), "indexOf de un valor ausente");
				}),
				check(type, "contains distingue entre null presente y null ausente", (list, impl) -> {
					isTrue(!list.contains(null), "contains(null) antes de insertarlo");
					list.add(null);
					isTrue(list.contains(null), "contains(null) tras insertarlo");
					equal(0, list.indexOf(null), "indexOf(null)");
				}),
				check(type, "clear vacía la lista", (list, impl) -> {
					list.addAll(List.of("a", "b"));
					list.clear();
					equal(0, list.size(), "size tras clear");
					isTrue(list.isEmpty(), "isEmpty tras clear");
				}),
				check(type, "el iterador recorre en orden de inserción", (list, impl) -> {
					list.addAll(List.of("a", "b", "c"));
					StringBuilder seen = new StringBuilder();
					for (Object element : list) {
						seen.append(element);
					}
					equal("abc", seen.toString(), "orden de recorrido");
				}),
				check(type, "iterator.remove borra el último elemento devuelto", (list, impl) -> {
					list.addAll(List.of("a", "b", "c"));
					Iterator<Object> iterator = list.iterator();
					iterator.next();
					iterator.next();
					iterator.remove();
					equal(2, list.size(), "size tras iterator.remove");
					equal("a", list.get(0), "primer elemento intacto");
					equal("c", list.get(1), "el segundo elemento se ha borrado");
				}),
				check(type, "el iterador es fail-fast ante modificaciones concurrentes", (list, impl) -> {
					list.addAll(List.of("a", "b"));
					Iterator<Object> iterator = list.iterator();
					iterator.next();
					list.add("c");
					throwsException(ConcurrentModificationException.class, iterator::next,
							"el iterador debe detectar la modificación concurrente");
				}),
				check(type, "el ListIterator va en ambos sentidos y puede fijar y añadir", (list, impl) -> {
					list.addAll(List.of("a", "b", "c"));
					ListIterator<Object> iterator = list.listIterator();
					equal("a", iterator.next(), "next");
					equal("a", iterator.previous(), "previous");
					equal(0, iterator.nextIndex(), "nextIndex tras previous");
					iterator.set("z");
					equal("z", list.get(0), "set del elemento actual");
					iterator.add("y");
					equal("y", list.get(0), "add inserta antes del cursor");
					equal(4, list.size(), "size tras add");
				}),
				check(type, "un iterador agotado lanza NoSuchElementException", (list, impl) -> {
					Iterator<Object> iterator = list.iterator();
					throwsException(NoSuchElementException.class, iterator::next,
							"next sobre una lista vacía");
				}),
				check(type, "equals y hashCode son coherentes entre implementaciones", (list, impl) -> {
					List<Object> elements = List.of("a", "b", "c");
					list.addAll(elements);
					ListType otherType = (impl == ListType.ARRAY_LIST) ? ListType.LINKED_LIST
							: ListType.ARRAY_LIST;
					MyList<Object> other = otherType.create();
					other.addAll(elements);
					isTrue(list.equals(other), "las dos implementaciones deben ser iguales");
					isTrue(other.equals(list), "la igualdad debe ser simétrica");
					equal(list.hashCode(), other.hashCode(), "hashCode debe coincidir");
					isTrue(!list.equals(List.of("a", "b")), "listas de distinto tamaño no son iguales");
				}),
				check(type, "toString usa el formato [a, b, c] del JDK", (list, impl) -> {
					list.addAll(List.of("a", "b", "c"));
					equal("[a, b, c]", list.toString(), "toString");
				}),
				check(type, "toArray devuelve una copia con el mismo contenido", (list, impl) -> {
					list.addAll(List.of("a", "b"));
					Object[] array = list.toArray();
					equal(2, array.length, "longitud del array");
					equal("a", array[0], "primer elemento");
					equal("b", array[1], "segundo elemento");
				}),
				check(type, "la lista crece sin perder elementos", (list, impl) -> {
					for (int i = 0; i < 1_000; i++) {
						list.add(i);
					}
					equal(1_000, list.size(), "size tras 1000 inserciones");
					equal(0, list.get(0), "primer elemento");
					equal(999, list.get(999), "último elemento");
					equal(500, list.indexOf(500), "indexOf en mitad de la lista");
				}),
				check(type, "add(index, e) inserta en cabeza, medio y cola", (list, impl) -> {
					list.add(0, "b");
					list.add(1, "c");
					list.add(0, "a");
					list.add(3, "d");
					equal("[a, b, c, d]", list.toString(), "orden tras insertar en distintas posiciones");
				}),
				check(type, "addAll, containsAll, removeAll y retainAll", (list, impl) -> {
					list.addAll(List.of("a", "b", "c"));
					isTrue(list.addAll(List.of("d", "e")), "addAll");
					equal("[a, b, c, d, e]", list.toString(), "contenido tras addAll");
					isTrue(list.containsAll(List.of("a", "c")), "containsAll");
					isTrue(!list.containsAll(List.of("a", "zzz")), "containsAll con un valor ausente");
					isTrue(list.removeAll(List.of("b", "d")), "removeAll");
					equal("[a, c, e]", list.toString(), "contenido tras removeAll");
					isTrue(list.retainAll(List.of("a", "e")), "retainAll");
					equal("[a, e]", list.toString(), "contenido tras retainAll");
				}),
				check(type, "sort ordena con el comparador indicado", (list, impl) -> {
					list.addAll(List.of("c", "a", "b"));
					list.sort((left, right) -> ((String) left).compareTo((String) right));
					equal("[a, b, c]", list.toString(), "contenido tras sort");
				}),
				check(type, "subList es una vista: lo que se muta en ella se ve en el padre", (list, impl) -> {
					list.addAll(List.of("a", "b", "c"));
					List<Object> view = list.subList(1, 3);
					view.set(0, "z");
					equal("z", list.get(1), "el cambio se refleja en el padre");
					equal("[z, c]", view.toString(), "la vista refleja el cambio");
				}),
				check(type, "toArray(T[]) reutiliza el array cuando hay sitio", (list, impl) -> {
					list.addAll(List.of("a", "b"));
					Object[] target = new Object[5];
					Object[] result = list.toArray(target);
					isTrue(result == target, "debe reutilizarse el array recibido");
					equal(null, result[2], "las posiciones sobrantes quedan a null");
				}));
	}

	/**
  * Las comprobaciones exclusivas de las implementaciones que además son un Deque.
  *
  * @return las comprobaciones exclusivas de {@link com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyLinkedList}
  * @param type implementación sobre la que se ejecuta
  */
	public static List<Check> dequeOnly(ListType type) {
		return List.of(
				check(type, "addFirst y addLast mantienen coherentes first y last", (list, impl) -> {
					MyLinkedList<Object> deque = asLinkedList(list);
					deque.addLast("b");
					deque.addFirst("a");
					equal("[a, b]", list.toString(), "orden de la lista");
					equal("a", deque.getFirst(), "getFirst");
					equal("b", deque.getLast(), "getLast");
				}),
				check(type, "uso como cola FIFO con offer y poll", (list, impl) -> {
					MyLinkedList<Object> deque = asLinkedList(list);
					isTrue(deque.offer("a"), "offer");
					deque.offer("b");
					equal("a", deque.poll(), "poll devuelve la cabeza");
					equal("b", deque.poll(), "poll devuelve la cabeza");
					equal(null, deque.poll(), "poll sobre una lista vacía devuelve null");
				}),
				check(type, "peek no modifica la lista y devuelve null si está vacía", (list, impl) -> {
					MyLinkedList<Object> deque = asLinkedList(list);
					equal(null, deque.peekFirst(), "peekFirst sobre una lista vacía");
					deque.add("a");
					equal("a", deque.peekFirst(), "peekFirst");
					equal("a", deque.peekLast(), "peekLast");
					equal(1, list.size(), "peek no modifica el tamaño");
				}),
				check(type, "push y pop funcionan como una pila", (list, impl) -> {
					MyLinkedList<Object> deque = asLinkedList(list);
					deque.push("a");
					deque.push("b");
					equal("[b, a]", list.toString(), "la pila crece por la cabeza");
					equal("b", deque.pop(), "pop devuelve la cabeza");
					equal("[a]", list.toString(), "la pila se reduce");
				}),
				check(type, "removeFirstOccurrence y removeLastOccurrence", (list, impl) -> {
					MyLinkedList<Object> deque = asLinkedList(list);
					deque.addAll(List.of("a", "b", "a"));
					isTrue(deque.removeFirstOccurrence("a"), "removeFirstOccurrence");
					equal("[b, a]", list.toString(), "contenido tras removeFirstOccurrence");
					isTrue(deque.removeLastOccurrence("a"), "removeLastOccurrence");
					equal("[b]", list.toString(), "contenido tras removeLastOccurrence");
					isTrue(!deque.removeFirstOccurrence("zzz"), "si no está, devuelve false");
				}),
				check(type, "descendingIterator recorre del último al primero", (list, impl) -> {
					MyLinkedList<Object> deque = asLinkedList(list);
					deque.addAll(List.of("a", "b", "c"));
					List<Object> seen = new ArrayList<>();
					Iterator<Object> iterator = deque.descendingIterator();
					while (iterator.hasNext()) {
						seen.add(iterator.next());
					}
					equal("[c, b, a]", seen.toString(), "orden inverso");
				}));
	}

	/**
  * Liga un cuerpo de comprobación a una lista nueva de la implementación indicada.
  *
  * @param type implementación sobre la que se ejecuta
  * @param name nombre de la comprobación
  * @param body cuerpo de la comprobación
  * @return la comprobación, lista para la suite
  */
	private static Check check(ListType type, String name, ListAssertion body) {
		return new Check(name, () -> body.verify(type.create(), type));
	}

	private static MyLinkedList<Object> asLinkedList(MyList<Object> list) {
		return (MyLinkedList<Object>) list;
	}
}
