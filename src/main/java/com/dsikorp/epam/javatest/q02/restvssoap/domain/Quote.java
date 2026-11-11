package com.dsikorp.epam.javatest.q02.restvssoap.domain;

import java.math.BigDecimal;

/**
 * El resultado del cálculo de un presupuesto.
 *
 * <p>Es <strong>el mismo dato</strong> en los dos protocolos: lo que cambia es únicamente cómo
 * viaja por el cable. Por eso el dominio no sabe nada de REST ni de SOAP, y por eso el mismo
 * servicio de negocio puede exponerse en los dos estilos sin duplicar una sola línea de lógica.
 *
 * @param sku referencia del producto
 * @param quantity unidades pedidas
 * @param unitPrice precio unitario sin descuento
 * @param total importe final tras el descuento
 */
public record Quote(String sku, int quantity, BigDecimal unitPrice, BigDecimal total) {
}
