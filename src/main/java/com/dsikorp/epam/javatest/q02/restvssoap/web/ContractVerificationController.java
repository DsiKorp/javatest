package com.dsikorp.epam.javatest.q02.restvssoap.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsikorp.epam.javatest.common.verification.VerificationReport;
import com.dsikorp.epam.javatest.q02.restvssoap.service.ContractVerificationService;

/**
 * Expone la suite de autoverificación de la pregunta 2.
 *
 * <p>Ejecuta las comprobaciones de los dos contratos (WSDL y OpenAPI), de los dos stubs y de la
 * equivalencia entre REST y SOAP, y devuelve el informe.</p>
 *
 * <p><strong>Ejemplo de uso con {@code curl}:</strong></p>
 * <pre>{@code
 * # Resumen del informe
 * curl -s -X POST http://localhost:8080/api/q2/verify \
 * | jq '{total, passed, failed, durationMs}'
 *
 * # Sólo las comprobaciones fallidas
 * curl http://localhost:8080/api/q2/verify | jq '.checks[] | select(.passed == false)'
 * }</pre>
 */
@RestController
@RequestMapping("/q2/verify")
public class ContractVerificationController {

	private final ContractVerificationService verificationService;

	/**
	 * Crea el controlador de la suite de verificación.
	 *
	 * @param verificationService el servicio que ejecuta la suite
	 */
	public ContractVerificationController(ContractVerificationService verificationService) {
		this.verificationService = verificationService;
	}

	/**
  * Ejecuta la suite y devuelve el informe.
  *
  * @return el informe de la suite de verificación
  */
	@GetMapping
	public ResponseEntity<VerificationReport> verify() {
		return ResponseEntity.ok(verificationService.run());
	}

	/**
  * Igual que {@link #verify()}, pero como POST.
  *
  * @return el informe de la suite de verificación
  */
	@PostMapping
	public ResponseEntity<VerificationReport> run() {
		return ResponseEntity.ok(verificationService.run());
	}
}
