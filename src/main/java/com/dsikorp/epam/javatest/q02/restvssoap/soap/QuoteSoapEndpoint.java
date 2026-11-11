package com.dsikorp.epam.javatest.q02.restvssoap.soap;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.dsikorp.epam.javatest.q02.restvssoap.domain.InvalidQuoteException;
import com.dsikorp.epam.javatest.q02.restvssoap.domain.PricingService;
import com.dsikorp.epam.javatest.q02.restvssoap.domain.Quote;

/**
 * La misma operación de negocio, expuesta en SOAP.
 *
 * <p>El endpoint no sabe nada de REST, y el de REST no sabe nada de SOAP. Los dos llaman al
 * mismo {@link PricingService}. La operación se identifica por el namespace y el nombre local
 * de la raíz del payload, que son exactamente los que declara el WSDL.</p>
 *
 * <p>En Spring WS 5 la operación se declara con {@link PayloadRoot} (la clave de búsqueda)
 * y {@link ResponsePayload} (la respuesta), y no con el clásico
 * {@code @PayloadMethod}: el nombre de la anotación cambia, pero el concepto es el mismo. La
 * clase va anotada con {@link Endpoint}, que es un {@code @Component} especializado: el
 * mapping de endpoints la localiza por esa anotación.</p>
 *
 * <p><strong>Ejemplo de la petición que espera este endpoint:</strong></p>
 * <pre>{@code
 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
 * <soapenv:Body>
 * <ns2:quote xmlns:ns2="http://example.com/quote">
 * <ns2:sku>SKU-1</ns2:sku>
 * <ns2:quantity>3</ns2:quantity>
 * <ns2:customerId>VIP-9</ns2:customerId>
 * </ns2:quote>
 * </soapenv:Body>
 * </soapenv:Envelope>
 * }</pre>
 */
@Endpoint
public class QuoteSoapEndpoint {

	private final PricingService pricingService;

	/**
	 * Crea el endpoint SOAP.
	 *
	 * @param pricingService el servicio de negocio, compartido con el endpoint REST
	 */
	public QuoteSoapEndpoint(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	/**
  * Calcula el presupuesto de un pedido.
  *
  * @param request la petición SOAP
  * @return el presupuesto calculado
  * @throws InvalidQuoteSoapFault si la petición no es válida
  */
	@PayloadRoot(namespace = SoapContract.NAMESPACE, localPart = SoapContract.OPERATION)
	@ResponsePayload
	public SoapQuoteResponse quote(@RequestPayload SoapQuoteRequest request) {
		try {
			Quote quote = pricingService.quote(request.getSku(), request.getQuantity(), request.getCustomerId());
			return new SoapQuoteResponse(quote.sku(), quote.quantity(), quote.unitPrice(), quote.total());
		}
		catch (InvalidQuoteException error) {
			throw new InvalidQuoteSoapFault(error.getMessage());
		}
	}
}
