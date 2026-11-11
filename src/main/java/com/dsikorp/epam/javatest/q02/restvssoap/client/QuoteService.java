package com.dsikorp.epam.javatest.q02.restvssoap.client;

import java.net.URI;

import com.dsikorp.epam.javatest.q02.restvssoap.contract.ContractDocuments;

/**
 * El localizador del servicio SOAP, al estilo del que genera `wsimport`.
 *
 */
public class QuoteService {

	private final ContractDocuments.OperationContract contract;

	private final String baseUrl;

	/**
  * El localizador del servicio SOAP, al estilo del que genera `wsimport`.
  */
	public QuoteService() {
		this(new ContractDocuments(), "http://localhost:8080");
	}

	/**
  * El localizador del servicio SOAP, al estilo del que genera `wsimport`.
  * @param contractDocuments el parser de los contratos
  * @param baseUrl URL base del servidor
  */
	public QuoteService(ContractDocuments contractDocuments, String baseUrl) {
		this.contract = contractDocuments.wsdl();
		this.baseUrl = baseUrl;
	}

	/**
  * Devuelve el contrato SOAP que describe el servicio.
  *
  * @return el contrato SOAP que describe el servicio
  */
	public ContractDocuments.OperationContract contract() {
		return contract;
	}

	/**
  * Devuelve la URL completa del servicio, resuelta desde el WSDL.
  *
  * @return la URL completa del servicio, resuelta desde el WSDL
  */
	public URI serviceAddress() {
		String location = contract.location();
		if (location.startsWith("http://") || location.startsWith("https://")) {
			return URI.create(location);
		}
		return URI.create(baseUrl).resolve(location);
	}

	/**
  * Devuelve el cliente del puerto.
  *
  * @return el cliente del puerto
  */
	public QuotePortType getQuotePort() {
		return new QuoteSoapClient(this);
	}
}
