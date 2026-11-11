package com.dsikorp.epam.javatest.q02.restvssoap.soap;

/**
 * Las constantes del contrato SOAP: el namespace del servicio y el nombre de la operación.
 *
 * <p>Están aquí, y no repetidas en cada clase, porque son <em>el contrato</em>: el WSDL, el
 * endpoint y el cliente tienen que hablar exactamente el mismo namespace. Si se deformara uno de
 * los tres, dejaría de ser la misma operación.</p>
 */
public final class SoapContract {

	/** Namespace del servicio, el {@code targetNamespace} del WSDL. */
	public static final String NAMESPACE = "http://example.com/quote";

	/** Nombre local de la operación, el {@code localPart} del WSDL. */
	public static final String OPERATION = "quote";

	/** Nombre del puerto SOAP, el {@code portType} del WSDL. */
	public static final String PORT_TYPE = "QuotePort";

	/** Nombre del servicio SOAP. */
	public static final String SERVICE = "QuoteService";

	/** {@code soapAction} de la operación. */
	public static final String SOAP_ACTION = NAMESPACE + "/" + OPERATION;

	private SoapContract() {
	}
}
