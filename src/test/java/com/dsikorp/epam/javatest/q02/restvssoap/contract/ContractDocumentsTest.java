package com.dsikorp.epam.javatest.q02.restvssoap.contract;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprueba que los dos contratos se leen y se interpretan bien.
 */
class ContractDocumentsTest {

	private final ContractDocuments contracts = new ContractDocuments();

	@Test
	@DisplayName("el WSDL declara la operación, el portType y la dirección")
	void readsTheWsdl() {
		ContractDocuments.OperationContract contract = contracts.wsdl();

		assertThat(contract.name()).isEqualTo("quote");
		assertThat(contract.portType()).isEqualTo("QuotePort");
		assertThat(contract.namespace()).isEqualTo("http://example.com/quote");
		assertThat(contract.requestElement()).isEqualTo("quote");
		assertThat(contract.responseElement()).isEqualTo("quoteResponse");
		assertThat(contract.location()).endsWith("/q2/soap/quote-service");
	}

	@Test
	@DisplayName("el esquema del WSDL define los campos de la petición y de la respuesta")
	void readsTheWsdlSchema() {
		ContractDocuments.OperationContract contract = contracts.wsdl();

		assertThat(contract.requestFields()).containsExactly("sku", "quantity", "customerId");
		assertThat(contract.responseFields()).containsExactly("sku", "quantity", "unitPrice", "total");
	}

	@Test
	@DisplayName("el OpenAPI declara la operación, el método y la ruta")
	void readsTheOpenApi() {
		ContractDocuments.RestContract contract = contracts.openApi();

		assertThat(contract.operationId()).isEqualTo("quote");
		assertThat(contract.openapiVersion()).isEqualTo("3.1.0");
		assertThat(contract.method()).isEqualTo("post");
		assertThat(contract.path()).isEqualTo("/q2/rest/quotes");
		assertThat(contract.serverUrl()).endsWith("/api");
	}

	@Test
	@DisplayName("el OpenAPI define los mismos campos que el WSDL")
	void readsTheOpenApiSchema() {
		ContractDocuments.RestContract contract = contracts.openApi();

		assertThat(contract.requestFields()).containsExactly("sku", "quantity", "customerId");
		assertThat(contract.responseFields()).containsExactly("sku", "quantity", "unitPrice", "total");
	}

	@Test
	@DisplayName("los dos contratos describen exactamente la misma operación")
	void bothContractsDescribeTheSameOperation() {
		ContractDocuments.OperationContract soap = contracts.wsdl();
		ContractDocuments.RestContract rest = contracts.openApi();

		assertThat(rest.operationId()).isEqualTo(soap.name());
		assertThat(rest.requestFields()).isEqualTo(soap.requestFields());
		assertThat(rest.responseFields()).isEqualTo(soap.responseFields());
	}

	@Test
	@DisplayName("el documento OpenAPI se publica tal cual, en YAML y en JSON")
	void exposesTheOpenApiDocument() {
		assertThat(contracts.openApiDocument()).containsKeys("openapi", "info", "servers", "paths", "components");
		assertThat(contracts.openApiDocument().get("paths")).isInstanceOf(java.util.Map.class);

		@SuppressWarnings("unchecked")
		java.util.Map<String, Object> paths = (java.util.Map<String, Object>) contracts.openApiDocument().get("paths");
		assertThat(paths).containsOnlyKeys("/q2/rest/quotes");
	}
}
