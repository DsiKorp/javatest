package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.verification;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyList;

/**
 * Una comprobación de la suite de verificación.
 *
 * <p>Recibe una lista vacía de la implementación que se está probando y debe dejar la lista en
 * el estado que necesite. Si la comprobación falla, lanza {@link AssertionError} con el motivo;
 * cualquier otra excepción se considera también un fallo.</p>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * ListAssertion compruebaElTamanio = (list, type) -> {
 *     list.add("a");
 *     list.add("b");
 *     equal(2, list.size(), "la lista debería tener 2 elementos");
 * };
 * }</pre>
 */
@FunctionalInterface
public interface ListAssertion {

	/**
	 * Ejecuta la comprobación.
	 *
	 * @param list lista vacía de la implementación bajo prueba
	 * @param type implementación bajo prueba
	 * @throws AssertionError si la comprobación no se supera
	 * @throws Exception si la comprobación falla por otra causa
	 */
	void verify(MyList<Object> list, ListType type) throws Exception;
}
