package com.dsikorp.epam.javatest.q02.restvssoap.dto;

import java.math.BigDecimal;

/**
 * Respuesta REST de un presupuesto.
 *
 * @param sku referencia del producto
 * @param quantity unidades pedidas
 * @param unitPrice precio unitario sin descuento
 * @param total importe final tras el descuento
 */
public record QuoteResponse(String sku, int quantity, BigDecimal unitPrice, BigDecimal total) {
}
