package com.dsikorp.epam.javatest.q02.restvssoap.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.dsikorp.epam.javatest.q02.restvssoap.contract.ContractDocuments;
import com.dsikorp.epam.javatest.q02.restvssoap.dto.QuoteRequest;
import com.dsikorp.epam.javatest.q02.restvssoap.dto.QuoteResponse;
import com.dsikorp.epam.javatest.q02.restvssoap.soap.SoapQuoteRequest;

/**
 * Comprueba los dos stubs contra el servicio de verdad, con el servidor levantado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuoteStubsTest {

	@LocalServerPort
	private int port;

	private final ContractDocuments contracts = new ContractDocuments();

	private String apiBaseUrl() {
		return "http://localhost:" + port + "/api";
	}

	private String origin() {
		return "http://localhost:" + port;
	}

	@Test
	@DisplayName("el stub REST llama a la ruta que declara el OpenAPI")
	void restStubUsesTheContractPath() {
		QuoteRestClient client = new QuoteRestClient(contracts, apiBaseUrl());

		assertThat(client.endpointUrl()).isEqualTo(apiBaseUrl() + "/q2/rest/quotes");
	}

	@Test
	@DisplayName("el stub SOAP llama a la dirección que declara el WSDL")
	void soapStubUsesTheWsdlAddress() {
		QuoteService service = new QuoteService(contracts, origin());

		assertThat(service.serviceAddress().toString()).isEqualTo(origin() + "/api/q2/soap/quote-service");
	}

	@Test
	@DisplayName("el stub REST obtiene el presupuesto")
	void restStubQuotes() {
		QuoteRestClient client = new QuoteRestClient(contracts, apiBaseUrl());

		QuoteResponse response = client.quote(new QuoteRequest("SKU-1", 3, "VIP-9"));

		assertThat(response.sku()).isEqualTo("SKU-1");
		assertThat(response.quantity()).isEqualTo(3);
		assertThat(response.unitPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
		assertThat(response.total()).isEqualByComparingTo(new BigDecimal("53.97"));
	}

	@Test
	@DisplayName("el stub SOAP obtiene el presupuesto")
	void soapStubQuotes() {
		QuotePortType port = new QuoteService(contracts, origin()).getQuotePort();

		var response = port.quote(new SoapQuoteRequest("SKU-1", 3, "VIP-9"));

		assertThat(response.getSku()).isEqualTo("SKU-1");
		assertThat(response.getQuantity()).isEqualTo(3);
		assertThat(response.getUnitPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
		assertThat(response.getTotal()).isEqualByComparingTo(new BigDecimal("53.97"));
	}

	@Test
	@DisplayName("los dos stubs devuelven exactamente lo mismo")
	void bothStubsAgree() {
		var porRest = new QuoteRestClient(contracts, apiBaseUrl()).quote(new QuoteRequest("SKU-2", 2, "C-1"));
		var porSoap = new QuoteService(contracts, origin()).getQuotePort()
			.quote(new SoapQuoteRequest("SKU-2", 2, "C-1"));

		assertThat(porRest.sku()).isEqualTo(porSoap.getSku());
		assertThat(porRest.quantity()).isEqualTo(porSoap.getQuantity());
		assertThat(porRest.unitPrice()).isEqualByComparingTo(porSoap.getUnitPrice());
		assertThat(porRest.total()).isEqualByComparingTo(porSoap.getTotal());
	}

	@Test
	@DisplayName("sin descuento, el total es el precio unitario por la cantidad")
	void noDiscountWithoutVip() {
		var response = new QuoteRestClient(contracts, apiBaseUrl()).quote(new QuoteRequest("SKU-2", 2, "C-1"));

		assertThat(response.total()).isEqualByComparingTo(new BigDecimal("99.00"));
	}

	@Test
	@DisplayName("un cliente VIP tiene un 10 % de descuento")
	void vipGetsDiscount() {
		var response = new QuoteRestClient(contracts, apiBaseUrl()).quote(new QuoteRequest("SKU-2", 2, "VIP-1"));

		assertThat(response.total()).isEqualByComparingTo(new BigDecimal("89.10"));
	}

	@Test
	@DisplayName("el stub REST falla si el producto no existe")
	void restStubFailsOnUnknownSku() {
		QuoteRestClient client = new QuoteRestClient(contracts, apiBaseUrl());

		assertThatThrownBy(() -> client.quote(new QuoteRequest("NOPE", 1, "C-1")))
			.hasMessageContaining("unknown sku");
	}
}
