package com.dsikorp.epam.javatest.q02.restvssoap.soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;

/**
 * Configuración del endpoint SOAP.
 *
 * <p>Tres piezas, y ninguna de ellas genera el WSDL:</p>
 * <ul>
 * <li>El <code>DefaultMethodEndpointAdapter</code> que Spring WS registra por defecto trae
 * ya los <em>payload processors</em> de JAXB, que son los que traducen entre el XML
 * del sobre SOAP y las clases JAXB del payload, en las dos direcciones.</li>
 * <li>El bean {@link WsdlDefinition} publica el WSDL en el {@code ?wsdl} del servlet. Se usa
 * {@link SimpleWsdl11Definition}, que sirve un WSDL <em>estático</em> tal cual: no necesita
 * wsdl4j ni ninguna fábrica de WSDL.</li>
 * <li>El WSDL es un fichero escrito a mano, en {@code q02/wsdl/quote-service.wsdl}. Es lo que
 * hace un servicio SOAP <em>legacy</em>: el contrato está congelado y el servidor se
 * implementa contra él.</li>
 * </ul>
 */
@Configuration
@EnableWs
public class SoapWebServiceConfiguration {

	/**
	 * Crea la configuración del endpoint SOAP.
	 */
	public SoapWebServiceConfiguration() {
	}

	/**
  * Registra el servlet que atiende el endpoint SOAP.
  *
  * <p>Hay que registrarlo a mano porque en Spring Boot 4 la auto-configuración de servicios web
  * se aparta en cuanto hay un {@code WsConfigurationSupport} en el contexto, que es lo que
  * crea {@code @EnableWs}. Es un cambio respecto a Boot 3, donde ambas cosas convivían.</p>
  *
  * @param context el contexto de la aplicación
  * @return el registro del servlet SOAP
  */
	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
			WebApplicationContext context) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet(context);
		ServletRegistrationBean<MessageDispatcherServlet> registration = new ServletRegistrationBean<>(servlet,
				"/q2/soap/*");
		registration.setLoadOnStartup(1);
		registration.setName("messageDispatcherServlet");
		return registration;
	}

	/**
  * Publica el WSDL del servicio de presupuestos.
  *
  * @return la definición del WSDL
  */
	@Bean("quote-service")
	public WsdlDefinition quoteServiceWsdlDefinition() {
		SimpleWsdl11Definition definition = new SimpleWsdl11Definition();
		definition.setWsdl(new ClassPathResource("q02/wsdl/quote-service.wsdl"));
		return definition;
	}
}
