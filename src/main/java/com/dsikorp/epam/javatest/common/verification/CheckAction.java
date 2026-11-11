package com.dsikorp.epam.javatest.common.verification;

/**
 * Una comprobación de una suite de verificación.
 *
 * <p>Si la comprobación falla debe lanzar {@link AssertionError} con el motivo; cualquier otra
 * excepción se considera también un fallo.</p>
 */
@FunctionalInterface
public interface CheckAction {

	/**
  * Ejecuta la comprobación.
  *
  * @throws AssertionError si la comprobación no se supera
  * @throws Exception si la comprobación falla por otra causa
  */
	void verify() throws Exception;
}
