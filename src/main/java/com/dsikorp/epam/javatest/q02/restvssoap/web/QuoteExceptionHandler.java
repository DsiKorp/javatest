package com.dsikorp.epam.javatest.q02.restvssoap.web;

import java.time.Instant;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dsikorp.epam.javatest.common.dto.ErrorResponse;
import com.dsikorp.epam.javatest.q02.restvssoap.domain.InvalidQuoteException;

/**
 * Manejo de errores del servicio de presupuestos.
 *
 * <p>Traduce el error de negocio a la respuesta que espera cada protocolo: en REST un
 * {@code 400 Bad Request}, y en SOAP un <em>SOAP Fault</em> (que lo produce el propio endpoint,
 * porque es el que conoce el sobre).</p>
 *
 * <p>El advice queda acotado a los controladores de esta pregunta, para que no capture las
 * excepciones de la pregunta 1. El prefijo es el del <em>módulo</em> entero y no el del
 * subpaquete {@code web}: Spring comprueba que el nombre de la clase del controlador empiece
 * por el prefijo, y el controlador REST vive en {@code ...restvssoap.rest}.</p>
 */
@RestControllerAdvice(basePackages = "com.dsikorp.epam.javatest.q02.restvssoap")
public class QuoteExceptionHandler {

	/**
	 * Crea el manejador de errores.
	 */
	public QuoteExceptionHandler() {
	}

	/**
  * Traduce el error de negocio a una respuesta 400.
  *
  * @param error el error de negocio
  * @param request la petición que lo originó
  * @return {@code 400 Bad Request} con el detalle del error
  */
	@ExceptionHandler(InvalidQuoteException.class)
	public ResponseEntity<ErrorResponse> handleInvalidQuote(InvalidQuoteException error,
			HttpServletRequest request) {
		ErrorResponse body = new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), error.getMessage(), request.getRequestURI(), List.of());
		return ResponseEntity.badRequest().body(body);
	}
}
