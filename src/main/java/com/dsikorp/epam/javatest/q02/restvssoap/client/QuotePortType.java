package com.dsikorp.epam.javatest.q02.restvssoap.client;

import com.dsikorp.epam.javatest.q02.restvssoap.soap.SoapQuoteRequest;
import com.dsikorp.epam.javatest.q02.restvssoap.soap.SoapQuoteResponse;

/**
 * El puerto del servicio SOAP: la interfaz que genera {@code wsimport} a partir del WSDL.
 *
 * <p>Es la parte del stub que ve el programador de negocio: un método por operación del
 * {@code portType}, con los tipos de payload. Todo lo demás (dónde está el servicio, cómo se
 * construye el sobre, cómo se manda) es maquinaria que vive en la implementación.</p>
 */
public interface QuotePortType {

	/**
  * Calcula el presupuesto de un pedido.
  *
  * @param request la petición
  * @return la respuesta
  */
	SoapQuoteResponse quote(SoapQuoteRequest request);
}
