package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.verification;

import java.util.Objects;

/**
 * Ayudas de aserción para la suite de verificación.
 *
 * <p>La suite vive en el código de producción (es lo que se expone por HTTP), así que no puede
 * apoyarse en AssertJ, que es una dependencia de test. Estas utilidades son su equivalente
 * mínimo: lanzan {@link AssertionError} con un mensaje explicativo.</p>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * isTrue(lista.size() == 3, "la lista debería tener 3 elementos");
 *
 * equal("a", lista.get(0), "el primer elemento debería ser 'a'");
 * // ante un fallo: "el primer elemento debería ser 'a' (esperado=a, obtenido=b)"
 *
 * throwsException(IndexOutOfBoundsException.class, () -> lista.get(99),
 *         "get con índice fuera de rango");
 * }</pre>
 */
public final class Verifications {

	private Verifications() {
	}

	/**
	 * Comprueba que una condición se cumple.
  *
	 * @param condition condición que debe cumplirse
	 * @param message mensaje si no se cumple
	 */
	public static void isTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	/**
	 * Comprueba que dos valores son iguales.
  *
	 * @param expected valor esperado
	 * @param actual valor obtenido
	 * @param message mensaje si no coinciden
	 */
	public static void equal(Object expected, Object actual, String message) {
		if (!Objects.equals(expected, actual)) {
			throw new AssertionError(message + " (esperado=" + expected + ", obtenido=" + actual + ")");
		}
	}

	/**
	 * Comprueba que una acción lanza la excepción esperada.
  *
	 * @param expectedType excepción esperada
	 * @param action acción que debe fallar
	 * @param message mensaje si no se lanza la excepción esperada
	 */
	public static void throwsException(Class<? extends Throwable> expectedType, Runnable action, String message) {
		try {
			action.run();
		}
		catch (Throwable thrown) {
			if (expectedType.isInstance(thrown)) {
				return;
			}
			throw new AssertionError(
					message + " (se esperaba " + expectedType.getSimpleName() + " y se lanzó "
							+ thrown.getClass().getSimpleName() + ")");
		}
		throw new AssertionError(message + " (no se lanzó ninguna excepción)");
	}
}
