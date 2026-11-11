package com.dsikorp.epam.javatest.q02.restvssoap.verification;

import static com.dsikorp.epam.javatest.common.verification.Verifications.equal;
import static com.dsikorp.epam.javatest.common.verification.Verifications.isTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.client.RestClient;

import com.dsikorp.epam.javatest.common.verification.Check;
import com.dsikorp.epam.javatest.q02.restvssoap.client.QuoteRestClient;
import com.dsikorp.epam.javatest.q02.restvssoap.client.QuoteService;
import com.dsikorp.epam.javatest.q02.restvssoap.client.QuoteSoapClient;
import com.dsikorp.epam.javatest.q02.restvssoap.contract.ContractDocuments;
import com.dsikorp.epam.javatest.q02.restvssoap.dto.QuoteRequest;
import com.dsikorp.epam.javatest.q02.restvssoap.soap.SoapContract;
import com.dsikorp.epam.javatest.q02.restvssoap.soap.SoapQuoteRequest;

/**
 * La batería de comprobaciones de la pregunta 2.
 *
 */
public final class ContractChecks {

	private static final String IMPLEMENTATION = "REST+SOAP";

	private ContractChecks() {
	}

	/**
  * Devuelve las comprobaciones de la pregunta 2.
  *
  * @param baseUrl el valor de baseUrl
  * @return las comprobaciones de la pregunta 2
  */
	public static List<Check> checks(String baseUrl) {
		List<Check> checks = new ArrayList<>();

		checks.add(new Check("el WSDL declara la operación 'quote'", () -> {
			ContractDocuments.OperationContract contract = new ContractDocuments().wsdl();
			equal("quote", contract.name(), "el nombre de la operación");
			equal("QuotePort", contract.portType(), "el nombre del portType");
			equal(SoapContract.NAMESPACE, contract.namespace(), "el namespace del servicio");
		}));

		checks.add(new Check("el esquema del WSDL define los campos de la petición y de la respuesta", () -> {
			ContractDocuments.OperationContract contract = new ContractDocuments().wsdl();
			equal(List.of("sku", "quantity", "customerId"), contract.requestFields(), "campos de la petición");
			equal(List.of("sku", "quantity", "unitPrice", "total"), contract.responseFields(), "campos de la respuesta");
		}));

		checks.add(new Check("el OpenAPI declara la operación 'quote'", () -> {
			ContractDocuments.RestContract contract = new ContractDocuments().openApi();
			equal("quote", contract.operationId(), "el operationId");
			equal("post", contract.method(), "el método HTTP");
			equal("/q2/rest/quotes", contract.path(), "la ruta");
			equal("3.1.0", contract.openapiVersion(), "la versión de OpenAPI");
		}));

		checks.add(new Check("los dos contratos describen la misma operación", () -> {
			ContractDocuments documents = new ContractDocuments();
			ContractDocuments.OperationContract soap = documents.wsdl();
			ContractDocuments.RestContract rest = documents.openApi();
			equal(soap.requestFields(), rest.requestFields(), "los campos de la petición deben coincidir");
			equal(soap.responseFields(), rest.responseFields(), "los campos de la respuesta deben coincidir");
			equal(soap.name(), rest.operationId(), "el nombre de la operación debe coincidir");
		}));

		checks.add(new Check("el stub SOAP construye un sobre SOAP válido", () -> {
			QuoteSoapClient client = new QuoteSoapClient(new QuoteService());
			String envelope = client.buildEnvelope(new SoapQuoteRequest("SKU-1", 3, "VIP-9"));

			var factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			var document = factory.newDocumentBuilder()
				.parse(new ByteArrayInputStream(envelope.getBytes(StandardCharsets.UTF_8)));

			var quotes = document.getElementsByTagNameNS(SoapContract.NAMESPACE, "quote");
			isTrue(quotes.getLength() == 1, "el sobre debe llevar exactamente un elemento 'quote'");
			var quote = (org.w3c.dom.Element) quotes.item(0);
			equal("SKU-1", quote.getElementsByTagNameNS(SoapContract.NAMESPACE, "sku").item(0).getTextContent(),
					"el sku dentro del sobre");
			equal("3", quote.getElementsByTagNameNS(SoapContract.NAMESPACE, "quantity").item(0).getTextContent(),
					"la cantidad dentro del sobre");
		}));

		checks.add(new Check("el stub REST invoca la URL que declara el OpenAPI", () -> {
			ContractDocuments documents = new ContractDocuments();
			QuoteRestClient client = new QuoteRestClient(documents, baseUrl);
			equal(baseUrl + documents.openApi().path(), client.endpointUrl(),
					"el cliente debe llamar a la ruta del contrato");
		}));

		checks.add(new Check("el soap:address del WSDL apunta al servlet SOAP", () -> {
			ContractDocuments.OperationContract contract = new ContractDocuments().wsdl();
			isTrue(contract.location().endsWith("/q2/soap/quote-service"),
					"la dirección del WSDL debe apuntar al servlet SOAP, no a la ruta REST");
		}));

		checks.add(new Check("los dos caminos devuelven el mismo resultado", () -> {
			ContractDocuments documents = new ContractDocuments();
			QuoteRestClient rest = new QuoteRestClient(documents, baseUrl);
			QuoteSoapClient soap = new QuoteSoapClient(new QuoteService(documents, originOf(baseUrl)));

			var porRest = rest.quote(new QuoteRequest("SKU-1", 3, "VIP-9"));
			var porSoap = soap.quote(new SoapQuoteRequest("SKU-1", 3, "VIP-9"));

			equal(porRest.sku(), porSoap.getSku(), "el sku");
			equal(porRest.quantity(), porSoap.getQuantity(), "la cantidad");
			equal(porRest.unitPrice(), porSoap.getUnitPrice(), "el precio unitario");
			equal(porRest.total(), porSoap.getTotal(), "el total");
		}));

		checks.add(new Check("el endpoint SOAP responde con un SOAP Fault ante una petición inválida", () -> {
			String envelope = """
					<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
					  <soapenv:Body>
					    <ns2:quote xmlns:ns2="http://example.com/quote">
					      <ns2:sku>NOPE</ns2:sku>
					      <ns2:quantity>3</ns2:quantity>
					      <ns2:customerId>C-1</ns2:customerId>
					    </ns2:quote>
					  </soapenv:Body>
					</soapenv:Envelope>""";

			String response = RestClient.create()
				.post()
				.uri(baseUrl + "/q2/soap/quote-service")
				.header("Content-Type", "text/xml; charset=utf-8")
				.body(envelope)
				.retrieve()
				.onStatus(status -> true, (request, res) -> {
				})
				.body(String.class);

			isTrue(response.contains("Fault"), "la respuesta debe contener un SOAP Fault: " + response);
			isTrue(response.contains("unknown sku"), "el fault debe explicar el motivo: " + response);
		}));

		checks.add(new Check("el endpoint REST responde 400 ante una petición inválida", () -> {
			String response = RestClient.create()
				.post()
				.uri(baseUrl + "/q2/rest/quotes")
				.body(new QuoteRequest("NOPE", 3, "C-1"))
				.retrieve()
				.onStatus(status -> true, (request, res) -> {
				})
				.body(String.class);

			isTrue(response.contains("unknown sku"), "la respuesta debe explicar el motivo: " + response);
		}));

		return checks;
	}

	/**
  * Devuelve la etiqueta de la implementación que aparece en el informe.
  *
  * @return la etiqueta de la implementación que aparece en el informe
  */
	public static String implementation() {
		return IMPLEMENTATION;
	}

	private static String originOf(String baseUrl) {
		int index = baseUrl.indexOf("/api");
		return index < 0 ? baseUrl : baseUrl.substring(0, index);
	}
}
