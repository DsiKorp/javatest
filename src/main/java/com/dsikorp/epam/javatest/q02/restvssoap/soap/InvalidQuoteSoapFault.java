package com.dsikorp.epam.javatest.q02.restvssoap.soap;

import org.springframework.ws.soap.SoapFaultException;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * El error de negocio translated al mundo SOAP: un <em>SOAP Fault</em> de tipo {@code client}.
 *
 * <p>El mismo fallo que en REST se responde con un {@code 400}, aquí viaja como un fault dentro
 * del sobre SOAP. Cambia el transporte, no la semántica: por eso el cliente generado tiene que
 * saber interpretar los dos.</p>
 */
@SoapFault(faultCode = FaultCode.CLIENT)
public class InvalidQuoteSoapFault extends SoapFaultException {

	/**
	 * Crea el SOAP Fault del error de negocio.
	 *
	 * @param message descripción del error, que viaja como el motivo del fault
	 */
	public InvalidQuoteSoapFault(String message) {
		super(message);
	}
}
