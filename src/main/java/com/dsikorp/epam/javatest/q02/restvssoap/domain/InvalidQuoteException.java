package com.dsikorp.epam.javatest.q02.restvssoap.domain;

/**
 * Error de negocio al calcular un presupuesto.
 *
 * <p>Es la traducción del mismo error a los dos mundos: en REST se responde con un
 * {@code 400 Bad Request} y en SOAP con un <em>SOAP Fault</em>. El error no sabe en qué
 * protocolo se ha producido, y por eso el mismo fallo se expresa en los dos dialectsos.
 */
public class InvalidQuoteException extends RuntimeException {

	/**
	 * Crea el error de negocio.
	 *
	 * @param message descripción del error
	 */
	public InvalidQuoteException(String message) {
		super(message);
	}
}
