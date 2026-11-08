package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.exception;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;

/**
 * Se lanza cuando no existe una lista con el identificador pedido (o existe pero es de otro
 * tipo de implementación). Se traduce a un {@code 404 Not Found}.
 */
public class ListNotFoundException extends RuntimeException {

	/** El identificador que no se encontró. */
	private final String id;

	/** La implementación que se esperaba encontrar. */
	private final ListType type;

	/**
	 * Crea la excepción con el identificador y la implementación esperados.
	 *
	 * @param id el identificador pedido
	 * @param type la implementación que se esperaba
	 */
	public ListNotFoundException(String id, ListType type) {
		super("No " + type + " list found with id '" + id + "'");
		this.id = id;
		this.type = type;
	}

	/**
	 * Devuelve el identificador que no se encontró.
	 *
	 * @return el identificador que no se encontró
	 */
	public String getId() {
		return id;
	}

	/**
	 * Devuelve la implementación que se esperaba.
	 *
	 * @return la implementación que se esperaba
	 */
	public ListType getType() {
		return type;
	}
}
