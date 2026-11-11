package com.dsikorp.epam.javatest.q02.restvssoap.contract;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.Yaml;

/**
 * Lee y parsea los dos contratos del servicio: el WSDL de SOAP y el OpenAPI de REST.
 */
@Component
public class ContractDocuments {

	/**
	 * Crea el parser de contratos.
	 */
	public ContractDocuments() {
	}

	/** Namespace del WSDL 1.1. */
	private static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";

	/** Namespace del binding SOAP 1.1. */
	private static final String SOAP_NS = "http://schemas.xmlsoap.org/wsdl/soap/";

	/** Namespace del esquema XML. */
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

	/** Ruta del WSDL en el classpath. */
	public static final String WSDL_LOCATION = "q02/wsdl/quote-service.wsdl";

	/** Ruta del documento OpenAPI en el classpath. */
	public static final String OPENAPI_LOCATION = "q02/contract/openapi.yaml";

	/**
  * La operación descrita por un contrato.
  *
  * @param name nombre de la operación
  * @param namespace namespace del contrato
  * @param portType nombre del portType que declara la operación
  * @param requestElement nombre del elemento de la petición
  * @param requestFields campos de la petición, en orden
  * @param responseElement nombre del elemento de la respuesta
  * @param responseFields campos de la respuesta, en orden
  * @param location dirección donde se publica el servicio
  */
	public record OperationContract(String name, String namespace, String portType, String requestElement,
			List<String> requestFields, String responseElement, List<String> responseFields, String location) {
	}

	/**
  * La operación descrita por el contrato REST.
  *
  * @param operationId identificador de la operación
  * @param openapiVersion versión del documento OpenAPI
  * @param method método HTTP
  * @param path ruta, relativa al servidor
  * @param serverUrl URL base del servidor
  * @param requestFields campos de la petición, en orden
  * @param responseFields campos de la respuesta, en orden
  */
	public record RestContract(String operationId, String openapiVersion, String method, String path, String serverUrl,
			List<String> requestFields, List<String> responseFields) {
	}

	/**
  * Devuelve el contrato SOAP, extraído del WSDL.
  *
  * @return el contrato SOAP, extraído del WSDL
  */
	public OperationContract wsdl() {
		Document document = parseXml(new ClassPathResource(WSDL_LOCATION));
		Element definitions = document.getDocumentElement();

		String namespace = definitions.getAttribute("targetNamespace");
		Element portType = firstElement(definitions, WSDL_NS, "portType");
		Element operation = firstElement(portType, WSDL_NS, "operation");
		String operationName = operation.getAttribute("name");

		Element requestMessage = firstElement(operation, WSDL_NS, "input");
		Element responseMessage = firstElement(operation, WSDL_NS, "output");
		String requestElement = messageElementName(definitions, requestMessage);
		String responseElement = messageElementName(definitions, responseMessage);

		Element schema = firstElement(firstElement(definitions, WSDL_NS, "types"), XSD_NS, "schema");
		Map<String, List<String>> elements = schemaElements(schema);

		Element address = firstElement(firstElement(firstElement(definitions, WSDL_NS, "service"), WSDL_NS, "port"),
				SOAP_NS, "address");

		return new OperationContract(operationName, namespace, portType.getAttribute("name"), requestElement,
				elements.getOrDefault(requestElement, List.of()), responseElement,
				elements.getOrDefault(responseElement, List.of()), address.getAttribute("location"));
	}

	/**
  * Devuelve el documento OpenAPI tal cual, ya parseado.
  *
  * @return el documento OpenAPI tal cual, ya parseado
  */
	@SuppressWarnings("unchecked")
	public Map<String, Object> openApiDocument() {
		return loadYaml(new ClassPathResource(OPENAPI_LOCATION));
	}

	/**
  * Devuelve el contrato REST, extraído del documento OpenAPI.
  *
  * @return el contrato REST, extraído del documento OpenAPI
  */
	@SuppressWarnings("unchecked")
	public RestContract openApi() {
		Map<String, Object> document = openApiDocument();

		List<Map<String, Object>> servers = (List<Map<String, Object>>) document.get("servers");
		String serverUrl = String.valueOf(servers.get(0).get("url"));

		Map<String, Object> paths = (Map<String, Object>) document.get("paths");
		Map.Entry<String, Object> pathEntry = paths.entrySet().iterator().next();
		String path = pathEntry.getKey();
		Map<String, Object> operations = (Map<String, Object>) pathEntry.getValue();
		Map.Entry<String, Object> operationEntry = operations.entrySet().iterator().next();
		String method = operationEntry.getKey();
		Map<String, Object> operation = (Map<String, Object>) operationEntry.getValue();
		String operationId = String.valueOf(operation.get("operationId"));

		Map<String, Object> schemas = (Map<String, Object>) ((Map<String, Object>) document.get("components"))
			.get("schemas");

		List<String> requestFields = schemaFields(schemas, operation, "requestBody");
		List<String> responseFields = schemaFields(schemas, operation, "responses");

		return new RestContract(operationId, String.valueOf(document.get("openapi")), method, path, serverUrl,
				requestFields, responseFields);
	}

	/**
  * Lee y parsea los dos contratos del servicio: el WSDL de SOAP y el OpenAPI de REST.
  */
	@SuppressWarnings("unchecked")
	private List<String> schemaFields(Map<String, Object> schemas, Map<String, Object> operation, String part) {
		Map<String, Object> content;
		if (part.equals("responses")) {
			Map<String, Object> responses = (Map<String, Object>) operation.get("responses");
			Map<String, Object> ok = (Map<String, Object>) responses.get("200");
			content = (Map<String, Object>) ok.get("content");
		}
		else {
			Map<String, Object> requestBody = (Map<String, Object>) operation.get("requestBody");
			content = (Map<String, Object>) requestBody.get("content");
		}
		Map<String, Object> media = (Map<String, Object>) content.values().iterator().next();
		Map<String, Object> schema = (Map<String, Object>) media.get("schema");

		String ref = String.valueOf(schema.get("$ref"));
		String schemaName = ref.substring(ref.lastIndexOf('/') + 1);
		Map<String, Object> definition = (Map<String, Object>) schemas.get(schemaName);
		Map<String, Object> properties = (Map<String, Object>) definition.get("properties");
		return new ArrayList<>(properties.keySet());
	}

	/**
  * Lee y parsea los dos contratos del servicio: el WSDL de SOAP y el OpenAPI de REST.
  */
	private String messageElementName(Element definitions, Element message) {
		String messageName = message.getAttribute("message");
		int colon = messageName.indexOf(':');
		String localName = colon < 0 ? messageName : messageName.substring(colon + 1);
		Element wsdlMessage = firstElement(definitions, WSDL_NS, "message", "name", localName);
		Element part = firstElement(wsdlMessage, WSDL_NS, "part");
		String element = part.getAttribute("element");
		int elementColon = element.indexOf(':');
		return elementColon < 0 ? element : element.substring(elementColon + 1);
	}

	/**
  * Lee y parsea los dos contratos del servicio: el WSDL de SOAP y el OpenAPI de REST.
  */
	private Map<String, List<String>> schemaElements(Element schema) {
		Map<String, List<String>> result = new LinkedHashMap<>();
		NodeList children = schema.getElementsByTagNameNS(XSD_NS, "element");
		for (int i = 0; i < children.getLength(); i++) {
			Element element = (Element) children.item(i);
			if (element.getParentNode() != schema) {
				continue;
			}
			List<String> fields = new ArrayList<>();
			NodeList members = element.getElementsByTagNameNS(XSD_NS, "element");
			for (int j = 0; j < members.getLength(); j++) {
				fields.add(members.item(j).getAttributes().getNamedItem("name").getNodeValue());
			}
			result.put(element.getAttribute("name"), fields);
		}
		return result;
	}

	private Element firstElement(Element parent, String namespace, String localName) {
		return firstElement(parent, namespace, localName, null, null);
	}

	private Element firstElement(Element parent, String namespace, String localName, String attributeName,
			String attributeValue) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element element = (Element) node;
			boolean nameMatches = localName.equals(element.getLocalName())
					&& (namespace == null || namespace.equals(element.getNamespaceURI()));
			if (nameMatches && attributeName != null
					&& !attributeValue.equals(element.getAttribute(attributeName))) {
				continue;
			}
			if (nameMatches) {
				return element;
			}
		}
		throw new IllegalStateException("No se encontró el elemento " + localName + " en el WSDL");
	}

	private Document parseXml(ClassPathResource resource) {
		try (InputStream input = resource.getInputStream()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			return factory.newDocumentBuilder().parse(input);
		}
		catch (Exception error) {
			throw new IllegalStateException("No se pudo leer el WSDL " + resource.getDescription(), error);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> loadYaml(ClassPathResource resource) {
		try (InputStream input = resource.getInputStream()) {
			return (Map<String, Object>) new Yaml().load(input);
		}
		catch (Exception error) {
			throw new IllegalStateException("No se pudo leer el OpenAPI " + resource.getDescription(), error);
		}
	}
}
