package com.dsikorp.epam.javatest.q02.restvssoap.contract;

import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Publica el contrato OpenAPI del servicio REST.
 *
 * <p>El equivalente en SOAP es el WSDL, que Spring WS publica en
 * {@code /q2/soap/quote-service.wsdl}. Los dos contratos son ficheros que se pueden descargar,
 * leer y pasarle a un generador de clientes.</p>
 *
 * <p>Es un endpoint de sólo lectura y sin estado: publicarlo no cambia nada del servicio, sólo
 * hace visible el contrato.</p>
 */
@RestController
@RequestMapping("/q2")
public class OpenApiContractController {

	private final ContractDocuments contractDocuments;

	/**
	 * Crea el controlador del contrato.
	 *
	 * @param contractDocuments el parser de los contratos
	 */
	public OpenApiContractController(ContractDocuments contractDocuments) {
		this.contractDocuments = contractDocuments;
	}

	/**
  * Devuelve el documento OpenAPI 3.1 del servicio REST.
  *
  * @return el documento OpenAPI 3.1 del servicio REST
  */
	@GetMapping(value = "/openapi.yaml", produces = "application/yaml")
	public Resource openApi() {
		return new ClassPathResource(ContractDocuments.OPENAPI_LOCATION);
	}

	/**
  * Devuelve el documento OpenAPI también en JSON.
  *
  * @return el documento OpenAPI también en JSON, por comodidad de las herramientas que sólo
  * saben leer JSON
  */
	@GetMapping(value = "/openapi.json", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> openApiAsJson() {
		return contractDocuments.openApiDocument();
	}
}
