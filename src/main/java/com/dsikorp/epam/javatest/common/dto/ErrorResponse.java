package com.dsikorp.epam.javatest.common.dto;

import java.time.Instant;
import java.util.List;

/**
 * Payload único de error de la API.
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * // {"timestamp":"2026-11-08T01:06:34.03Z","status":404,"error":"Not Found",
 * //  "message":"No ARRAY_LIST list found with id 'no-existe'",
 * //  "path":"/api/q1/lists/arraylist/no-existe","details":[]}
 * ErrorResponse error = new ErrorResponse(Instant.now(), 404, "Not Found",
 * "No ARRAY_LIST list found with id 'no-existe'",
 * "/api/q1/lists/arraylist/no-existe", List.of());
 * }</pre>
 *
 * @param timestamp momento en que se produjo el error
 * @param status código de estado HTTP
 * @param error razón textual del estado
 * @param message descripción del error
 * @param path ruta que originó el error
 * @param details detalle adicional, por ejemplo errores de validación de campos
 */
public record ErrorResponse(Instant timestamp, int status, String error, String message, String path,
		List<String> details) {
}
