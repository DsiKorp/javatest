package com.dsikorp.epam.javatest.q02.restvssoap.client;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.web.client.RestClient;

import com.dsikorp.epam.javatest.q02.restvssoap.soap.SoapContract;
import com.dsikorp.epam.javatest.q02.restvssoap.soap.SoapQuoteRequest;
import com.dsikorp.epam.javatest.q02.restvssoap.soap.SoapQuoteResponse;

/**
 * Stub del cliente SOAP, escrito al estilo que produce `wsimport`.
 *
 */
public class QuoteSoapClient implements QuotePortType {

	/** Namespace del sobre SOAP 1.1. */
	private static final String SOAP_ENV_NS = "http://schemas.xmlsoap.org/soap/envelope/";

	private final QuoteService quoteService;

	private final RestClient restClient;

	/**
  * Stub del cliente SOAP, escrito al estilo que produce `wsimport`.
  * @param quoteService el localizador, que resuelve la dirección del WSDL
  */
	public QuoteSoapClient(QuoteService quoteService) {
		this.quoteService = quoteService;
		this.restClient = RestClient.create();
	}

	@Override
	public SoapQuoteResponse quote(SoapQuoteRequest request) {
		String envelope = buildEnvelope(request);
		String body = restClient.post()
			.uri(quoteService.serviceAddress())
			.header("Content-Type", "text/xml; charset=utf-8")
			.header("SOAPAction", SoapContract.SOAP_ACTION)
			.body(envelope)
			.retrieve()
			.body(String.class);
		return readResponse(body);
	}

	/**
  * Construye el sobre SOAP de la petición.
  *
  * @param request el valor de request
  * @return construye el sobre SOAP de la petición
  */
	public String buildEnvelope(SoapQuoteRequest request) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			var document = factory.newDocumentBuilder().newDocument();

			var envelope = document.createElementNS(SOAP_ENV_NS, "soapenv:Envelope");
			document.appendChild(envelope);
			var body = document.createElementNS(SOAP_ENV_NS, "soapenv:Body");
			envelope.appendChild(body);
			var quote = document.createElementNS(SoapContract.NAMESPACE, "ns2:quote");
			body.appendChild(quote);

			append(document, quote, "sku", request.getSku());
			append(document, quote, "quantity", String.valueOf(request.getQuantity()));
			append(document, quote, "customerId", request.getCustomerId());

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		}
		catch (Exception error) {
			throw new IllegalStateException("No se pudo construir el sobre SOAP", error);
		}
	}

	/**
  * Lee la respuesta del servicio.
  *
  * @param body el valor de body
  * @return lee la respuesta del servicio
  */
	public SoapQuoteResponse readResponse(String body) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			var document = factory.newDocumentBuilder()
				.parse(new ByteArrayInputStream(body.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

			var elements = document.getElementsByTagNameNS(SoapContract.NAMESPACE, "quoteResponse");
			if (elements.getLength() == 0) {
				throw new IllegalStateException("La respuesta SOAP no contiene quoteResponse: " + body);
			}
			var response = (org.w3c.dom.Element) elements.item(0);

			SoapQuoteResponse quote = new SoapQuoteResponse();
			quote.setSku(text(response, "sku"));
			quote.setQuantity(Integer.parseInt(text(response, "quantity")));
			quote.setUnitPrice(new BigDecimal(text(response, "unitPrice")));
			quote.setTotal(new BigDecimal(text(response, "total")));
			return quote;
		}
		catch (IllegalStateException error) {
			throw error;
		}
		catch (Exception error) {
			throw new IllegalStateException("No se pudo leer la respuesta SOAP", error);
		}
	}

	/**
  * Devuelve la URL a la que llama este cliente.
  *
  * @return la URL a la que llama este cliente
  */
	public URI endpointUrl() {
		return quoteService.serviceAddress();
	}

	private void append(org.w3c.dom.Document document, org.w3c.dom.Element parent, String name, String value) {
		var element = document.createElementNS(SoapContract.NAMESPACE, "ns2:" + name);
		element.setTextContent(value == null ? "" : value);
		parent.appendChild(element);
	}

	private String text(org.w3c.dom.Element parent, String name) {
		var elements = parent.getElementsByTagNameNS(SoapContract.NAMESPACE, name);
		return elements.getLength() == 0 ? null : elements.item(0).getTextContent();
	}
}
