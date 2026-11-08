package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.web;

import java.time.Instant;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ErrorResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.exception.InvalidListTypeException;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.exception.ListNotFoundException;

/**
 * Manejo centralizado de errores de la API.
 *
 * <p>Todos los errores salen con el mismo shape ({@link ErrorResponse}), para que quien consume
 * la API no tenga que distinguir entre formatos distintos.</p>
 *
 * <p>El advice queda acotado a los controladores de este punto del examen
 * ({@code basePackages}). Así, cuando se añada la pregunta #2 con su propio manejo de errores,
 * ambos conviven sin que uno capture las excepciones del otro.</p>
 */
@RestControllerAdvice(basePackages = "com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.web")
public class GlobalExceptionHandler {

	/**
	 * Crea el manejador de errores de este punto del examen.
	 */
	public GlobalExceptionHandler() {
	}

	/**
	 * Maneja una lista que no existe.
  *
	 * @param error lista inexistente
	 * @param request petición que originó el error
	 * @return {@code 404 Not Found}
	 */
	@ExceptionHandler(ListNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ListNotFoundException error, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, error.getMessage(), request, List.of());
	}

	/**
	 * Maneja un tipo de lista que no corresponde a ninguna implementación.
  *
	 * @param error tipo de lista desconocido
	 * @param request petición que originó el error
	 * @return {@code 400 Bad Request}
	 */
	@ExceptionHandler(InvalidListTypeException.class)
	public ResponseEntity<ErrorResponse> handleInvalidType(InvalidListTypeException error,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, error.getMessage(), request, List.of());
	}

	/**
	 * Maneja un índice que está fuera del rango de la lista.
  *
	 * @param error índice fuera de rango
	 * @param request petición que originó el error
	 * @return {@code 400 Bad Request}
	 */
	@ExceptionHandler(IndexOutOfBoundsException.class)
	public ResponseEntity<ErrorResponse> handleIndexOutOfBounds(IndexOutOfBoundsException error,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, error.getMessage(), request, List.of());
	}

	/**
	 * Maneja un parámetro de la petición que no cumple las restricciones de validación.
  *
	 * @param error violaciones de validación de los parámetros de la petición
	 * @param request petición que originó el error
	 * @return {@code 400 Bad Request}
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException error,
			HttpServletRequest request) {
		List<String> details = error.getConstraintViolations()
			.stream()
			.map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
			.toList();
		return build(HttpStatus.BAD_REQUEST, "Invalid request parameter", request, details);
	}

	/**
	 * Maneja un cuerpo de la petición que no se puede leer.
  *
	 * @param error cuerpo de la petición ilegible
	 * @param request petición que originó el error
	 * @return {@code 400 Bad Request}
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException error,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "Malformed request body", request, List.of());
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request,
			List<String> details) {
		ErrorResponse body = new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message,
				request.getRequestURI(), details);
		return ResponseEntity.status(status).body(body);
	}
}
