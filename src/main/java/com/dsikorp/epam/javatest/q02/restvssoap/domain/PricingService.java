package com.dsikorp.epam.javatest.q02.restvssoap.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * El servicio de negocio: calcula el presupuesto de un pedido.
 */
@Service
public class PricingService {

	/**
	 * Crea el servicio de precios.
	 */
	public PricingService() {
	}

	/** Catálogo de precios, fijo para que la verificación sea determinista. */
	private static final Map<String, BigDecimal> PRICES = Map.of(
			"SKU-1", new BigDecimal("19.99"),
			"SKU-2", new BigDecimal("49.50"),
			"SKU-3", new BigDecimal("5.25"));

	/** Descuento aplicado a los clientes VIP. */
	private static final BigDecimal VIP_DISCOUNT = new BigDecimal("0.10");

	private static final String VIP_PREFIX = "VIP-";

	/**
  * Calcula el presupuesto de un pedido.
  *
  * @param sku el valor de sku
  * @param quantity el valor de quantity
  * @param customerId el valor de customerId
  * @return el presupuesto de un pedido
  */
	public Quote quote(String sku, int quantity, String customerId) {
		if (quantity <= 0) {
			throw new InvalidQuoteException("quantity must be greater than 0, was " + quantity);
		}
		BigDecimal unitPrice = PRICES.get(sku);
		if (unitPrice == null) {
			throw new InvalidQuoteException("unknown sku: " + sku);
		}

		BigDecimal discount = isVip(customerId) ? VIP_DISCOUNT : BigDecimal.ZERO;
		BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity))
			.multiply(BigDecimal.ONE.subtract(discount))
			.setScale(2, RoundingMode.HALF_UP);

		return new Quote(sku, quantity, unitPrice, total);
	}

	/**
  * Indica si el cliente tiene descuento VIP.
  *
  * @param customerId el valor de customerId
  * @return el cliente tiene descuento VIP
  */
	public boolean isVip(String customerId) {
		return customerId != null && customerId.startsWith(VIP_PREFIX);
	}
}
