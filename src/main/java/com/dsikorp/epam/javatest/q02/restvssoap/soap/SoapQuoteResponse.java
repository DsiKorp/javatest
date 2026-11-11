package com.dsikorp.epam.javatest.q02.restvssoap.soap;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Payload SOAP de la respuesta de presupuesto.
 *
 * <p>El elemento se llama {@code quoteResponse}, tal y como lo declara el WSDL.</p>
 */
@XmlRootElement(name = "quoteResponse", namespace = SoapContract.NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "quoteResponse", namespace = SoapContract.NAMESPACE,
		propOrder = { "sku", "quantity", "unitPrice", "total" })
public class SoapQuoteResponse {

	@XmlElement(name = "sku", namespace = SoapContract.NAMESPACE, required = true)
	private String sku;

	@XmlElement(name = "quantity", namespace = SoapContract.NAMESPACE, required = true)
	private int quantity;

	@XmlElement(name = "unitPrice", namespace = SoapContract.NAMESPACE, required = true)
	private BigDecimal unitPrice;

	@XmlElement(name = "total", namespace = SoapContract.NAMESPACE, required = true)
	private BigDecimal total;

	/**
	 * Constructor vacío, obligatorio para JAXB.
	 */
	public SoapQuoteResponse() {
	}

	/**
	 * Constructor con los valores de la respuesta.
	 *
	 * @param sku la referencia del producto
	 * @param quantity las unidades pedidas
	 * @param unitPrice el precio unitario sin descuento
	 * @param total el importe final tras el descuento
	 */
	public SoapQuoteResponse(String sku, int quantity, BigDecimal unitPrice, BigDecimal total) {
		this.sku = sku;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.total = total;
	}

	/**
	 * Devuelve la referencia del producto.
	 *
	 * @return la referencia del producto
	 */
	public String getSku() {
		return sku;
	}

	/**
	 * Fija la referencia del producto.
	 *
	 * @param sku la referencia del producto
	 */
	public void setSku(String sku) {
		this.sku = sku;
	}

	/**
	 * Devuelve las unidades pedidas.
	 *
	 * @return las unidades pedidas
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Fija las unidades pedidas.
	 *
	 * @param quantity las unidades pedidas
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Devuelve el precio unitario sin descuento.
	 *
	 * @return el precio unitario sin descuento
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * Fija el precio unitario sin descuento.
	 *
	 * @param unitPrice el precio unitario sin descuento
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * Devuelve el importe final tras el descuento.
	 *
	 * @return el importe final tras el descuento
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * Fija el importe final tras el descuento.
	 *
	 * @param total el importe final tras el descuento
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
