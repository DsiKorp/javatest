package com.dsikorp.epam.javatest.q02.restvssoap.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Payload SOAP de la petición de presupuesto.
 *
 * <p>Es la clase que genera <code>wsimport</code> a partir del WSDL: un JavaBean sin
 * constructor con argumentos, con getters y setters, anotado con JAXB. La forma es
 * deliberadamente la clásica de un stub generado, no la de un record.</p>
 *
 * <p>El nombre del elemento ({@code quote}) y el namespace ({@code http://example.com/quote})
 * tienen que coincidir con los que declara el WSDL: son el contrato.</p>
 */
@XmlRootElement(name = "quote", namespace = SoapContract.NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "quote", namespace = SoapContract.NAMESPACE, propOrder = { "sku", "quantity", "customerId" })
public class SoapQuoteRequest {

	@XmlElement(name = "sku", namespace = SoapContract.NAMESPACE, required = true)
	private String sku;

	@XmlElement(name = "quantity", namespace = SoapContract.NAMESPACE, required = true)
	private int quantity;

	@XmlElement(name = "customerId", namespace = SoapContract.NAMESPACE, required = true)
	private String customerId;

	/**
	 * Constructor vacío, obligatorio para JAXB.
	 */
	public SoapQuoteRequest() {
	}

	/**
	 * Constructor con los valores de la petición.
	 *
	 * @param sku la referencia del producto
	 * @param quantity las unidades pedidas
	 * @param customerId el identificador del cliente
	 */
	public SoapQuoteRequest(String sku, int quantity, String customerId) {
		this.sku = sku;
		this.quantity = quantity;
		this.customerId = customerId;
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
	 * Devuelve el identificador del cliente.
	 *
	 * @return el identificador del cliente
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * Fija el identificador del cliente.
	 *
	 * @param customerId el identificador del cliente
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}
