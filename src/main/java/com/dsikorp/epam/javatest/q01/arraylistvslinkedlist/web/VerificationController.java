package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.VerificationReport;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.service.VerificationService;

/**
 * Expone la suite de autoverificación.
 *
 * <p>Es el endpoint con el que se "hace la prueba": ejecuta la batería de comprobaciones del
 * contrato de {@code List} contra las dos implementaciones y devuelve el informe.</p>
 *
 * <p>El prefijo {@code /api/q1} identifica el punto del examen al que pertenece el recurso, de
 * forma que cada pregunta del listado exponga su propio espacio de URLs sin colisionar.</p>
 *
 * <p><strong>Ejemplo de uso con {@code curl}:</strong></p>
 * <pre>{@code
 * # Resumen del informe
 * curl -X POST http://localhost:8080/api/q1/verify \
 *      | jq '{total, passed, failed, durationMs}'
 * # → { "total": 52, "passed": 52, "failed": 0, "durationMs": 15 }
 *
 * # Sólo las comprobaciones fallidas (vacío si todo va bien)
 * curl http://localhost:8080/api/q1/verify | jq '.checks[] | select(.passed == false)'
 *
 * # El detalle completo, agrupado por implementación
 * curl http://localhost:8080/api/q1/verify \
 *      | jq -r '.checks[] | "\(.implementation) | \(.passed) | \(.name)"'
 * }</pre>
 */
@RestController
@RequestMapping("/verify")
public class VerificationController {

	private final VerificationService verificationService;

	/**
	 * Crea el controlador con su servicio.
	 *
	 * @param verificationService el servicio que ejecuta la suite
	 */
	public VerificationController(VerificationService verificationService) {
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
	 * Igual que {@link #verify()}, pero como POST para quien prefiera no lanzar peticiones de
	 * lectura desde el navegador.
	 *
	 * @return el informe de la suite de verificación
	 */
	@PostMapping
	public ResponseEntity<VerificationReport> run() {
		return ResponseEntity.ok(verificationService.run());
	}
}
