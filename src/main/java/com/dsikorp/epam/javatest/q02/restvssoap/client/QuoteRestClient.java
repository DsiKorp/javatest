package com.dsikorp.epam.javatest.q02.restvssoap.client;

import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestClient;

import com.dsikorp.epam.javatest.q02.restvssoap.contract.ContractDocuments;
import com.dsikorp.epam.javatest.q02.restvssoap.dto.QuoteRequest;
import com.dsikorp.epam.javatest.q02.restvssoap.dto.QuoteResponse;

/**
 * Stub del cliente REST, escrito al estilo que produce `openapi-generator`.
 *
 */
public class QuoteRestClient {

	private final ContractDocuments.RestContract contract;

	private final String baseUrl;

	private final RestClient restClient;

	/**
  * Stub del cliente REST, escrito al estilo que produce `openapi-generator`.
  * @param contractDocuments el parser de los contratos
  */
	public QuoteRestClient(ContractDocuments contractDocuments) {
		this(contractDocuments, contractDocuments.openApi().serverUrl());
	}

	/**
  * Stub del cliente REST, escrito al estilo que produce `openapi-generator`.
  * @param contractDocuments el parser de los contratos
  * @param baseUrl URL base del servidor
  */
	public QuoteRestClient(ContractDocuments contractDocuments, String baseUrl) {
		this.contract = contractDocuments.openApi();
		this.baseUrl = baseUrl;
		this.restClient = RestClient.builder().baseUrl(baseUrl).build();
	}

	/**
  * Devuelve el contrato REST que gobierna este cliente.
  *
  * @return el contrato REST que gobierna este cliente
  */
	public ContractDocuments.RestContract contract() {
		return contract;
	}

	/**
  * Devuelve la URL completa a la que llama este cliente.
  *
  * @return la URL completa a la que llama este cliente
  */
	public String endpointUrl() {
		return baseUrl + contract.path();
	}

	/**
  * Stub del cliente REST, escrito al estilo que produce `openapi-generator`.
  * @return el presupuesto calculado
  * @param request la petición
  */
	public QuoteResponse quote(QuoteRequest request) {
		return restClient.post()
			.uri(contract.path())
			.body(request)
			.retrieve()
			.body(QuoteResponse.class);
	}

	/**
  * Devuelve la respuesta en crudo, tal y como la devuelve el servicio.
  *
  * @param request el valor de request
  * @return la respuesta en crudo, tal y como la devuelve el servicio
  */
	@SuppressWarnings("unchecked")
	public Map<String, Object> quoteRaw(QuoteRequest request) {
		return restClient.post()
			.uri(contract.path())
			.body(request)
			.retrieve()
			.body(Map.class);
	}

	/**
  * Devuelve los campos de la respuesta según el contrato.
  *
  * @return los campos de la respuesta según el contrato
  */
	public List<String> responseFields() {
		return contract.responseFields();
	}
}
