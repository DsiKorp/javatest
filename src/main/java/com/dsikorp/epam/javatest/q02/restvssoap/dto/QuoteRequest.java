package com.dsikorp.epam.javatest.q02.restvssoap.dto;

/**
 * Petición REST de un presupuesto.
 *
 * <p>Es el equivalente JSON del elemento {@code quote} del WSDL: mismos campos, otro dialecto.
 *
 * @param sku referencia del producto
 * @param quantity unidades pedidas
 * @param customerId identificador del cliente
 */
public record QuoteRequest(String sku, int quantity, String customerId) {
}
