package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections;

import java.util.Locale;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.exception.InvalidListTypeException;

/**
 * Catálogo de las implementaciones de lista disponibles, con la fábrica que las instancia.
 *
 * <p>Permite que la capa web elija la implementación a través de la URL
 * ({@code /api/q1/lists/arraylist/...} frente a {@code /api/q1/lists/linkedlist/...}) sin que el
 * controlador conozca las clases concretas.</p>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * // Interpretar el texto de la URL
 * ListType tipo = ListType.parse("array-list");   // → ListType.ARRAY_LIST
 * ListType.parse("linkedlist");                   // → ListType.LINKED_LIST
 * ListType.parse("vector");                       // → InvalidListTypeException
 *
 * // Instanciar la implementación correspondiente
 * MyList<String> lista = ListType.ARRAY_LIST.create();
 * lista.add("a");
 *
 * // Recorrer todas las implementaciones
 * for (ListType cada : ListType.values()) {
 * System.out.println(cada.create());   // [] y []
 * }
 * }</pre>
 */
public enum ListType {

	/** Lista respaldada por un array redimensionable. */
	ARRAY_LIST {
	@Override
		public <E> MyList<E> create() {
			return new MyArrayList<>();
		}
	},

	/** Lista respaldada por una cadena de nodos doblemente enlazados. */
	LINKED_LIST {
	@Override
		public <E> MyList<E> create() {
			return new MyLinkedList<>();
		}
	};

	/**
  * Crea una lista nueva y vacía de esta implementación.
  *
  * @param <E> tipo de los elementos
  * @return una lista vacía
  */
	public abstract <E> MyList<E> create();

	/**
  * Interpreta el texto de la URL como un tipo de lista. Acepta, sin distinguir mayúsculas,
  * guiones y guiones bajos: {@code arraylist}, {@code array-list}, {@code array_list},
  * {@code linkedlist}, {@code linked-list}...
  *
  * @param raw texto recibido
  * @return el tipo correspondiente
  * @throws InvalidListTypeException si el texto no corresponde a ninguna implementación
  */
	public static ListType parse(String raw) {
		String normalized = normalize(raw);
		for (ListType type : values()) {
			if (normalize(type.name()).equals(normalized)) {
				return type;
			}
		}
		throw new InvalidListTypeException(raw, supportedTypes());
	}

	/**
  * Los nombres de los tipos soportados, separados por comas.
  *
  * @return los nombres de los tipos soportados, separados por comas
  */
	public static String supportedTypes() {
		StringBuilder sb = new StringBuilder();
		for (ListType type : values()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(type.name());
		}
		return sb.toString();
	}

	private static String normalize(String raw) {
		if (raw == null) {
			return "";
		}
		return raw.trim().toLowerCase(Locale.ROOT).replace("-", "").replace("_", "").replace(" ", "");
	}
}
