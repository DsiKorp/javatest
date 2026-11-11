package com.dsikorp.epam.javatest.q02.restvssoap.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsikorp.epam.javatest.q02.restvssoap.domain.PricingService;
import com.dsikorp.epam.javatest.q02.restvssoap.domain.Quote;
import com.dsikorp.epam.javatest.q02.restvssoap.dto.QuoteRequest;
import com.dsikorp.epam.javatest.q02.restvssoap.dto.QuoteResponse;

/**
 * La operación de negocio expuesta en REST.
 *
 * <p>Es el mismo servicio de negocio que expone el endpoint SOAP, con un resultado idéntico. La
 * única diferencia entre las dos APIs está en el contrato y en el transporte.</p>
 */
@RestController
@RequestMapping("/q2/rest/quotes")
public class QuoteRestController {

	private final PricingService pricingService;

	/**
	 * Crea el controlador REST.
	 *
	 * @param pricingService el servicio de negocio, compartido con el endpoint SOAP
	 */
	public QuoteRestController(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	/**
  * Calcula el presupuesto de un pedido.
  *
  * @param request la petición
  * @return el presupuesto calculado
  */
	@PostMapping
	public ResponseEntity<QuoteResponse> quote(@RequestBody QuoteRequest request) {
		Quote quote = pricingService.quote(request.sku(), request.quantity(), request.customerId());
		return ResponseEntity
			.ok(new QuoteResponse(quote.sku(), quote.quantity(), quote.unitPrice(), quote.total()));
	}
}
