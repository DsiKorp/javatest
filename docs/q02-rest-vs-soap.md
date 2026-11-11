# REST frente a SOAP — los contratos (WSDL y OpenAPI)

Pregunta 2 del listado de EPAM. El enunciado es explícito sobre qué evalúa de verdad:

> «Las diferencias teóricas —como que "SOAP es un protocolo y REST no"—, aunque resultan
> interesantes, no aportan mucho a la hora de evaluar la experiencia real de un candidato. Un
> aspecto que suele pasarse por alto [...] es el uso de archivos **WSDL**. Los archivos WSDL se
> emplean habitualmente para generar *stubs* de comunicación en Java. ¿Existe alguna herramienta o
> tecnología que permita hacer lo mismo con los *endpoints* de REST? La respuesta es sí: se llama
> **OpenAPI**. Documentar los servicios web es una de las tareas más importantes al implementar
> funcionalidades.»

Así que lo que hay que demostrar no es recitar diferencias, sino **saber documentar una API y
generar stubs a partir de su contrato**, en los dos estilos.

**Lo que hace este módulo:** expone **la misma operación de negocio** en REST y en SOAP, publica
**los dos contratos** (OpenAPI y WSDL), incluye **los dos stubs** escritos con la forma que
produce cada generador, y tiene una suite que comprueba que **los dos contratos describen la
misma operación** y que los dos caminos devuelven lo mismo.

---

## 1. La operación

`quote(sku, quantity, customerId)` → `sku, quantity, unitPrice, total`.

El negocio está en `domain/PricingService` y **no sabe nada de los protocolos**. Los dos
endpoints lo llaman. Si el negocio fuera distinto en cada uno, comparar REST y SOAP no probaría
nada: compararía dos negocios distintos.

---

## 2. Los dos contratos

| | SOAP | REST |
|---|---|---|
| Fichero | `src/main/resources/q02/wsdl/quote-service.wsdl` | `src/main/resources/q02/contract/openapi.yaml` |
| Se publica en | `GET /api/q2/soap/quote-service.wsdl` | `GET /api/q2/openapi.yaml` (y `.json`) |
| Formato | WSDL 1.1 + XML Schema | OpenAPI 3.1 |
| Genera stubs con | `wsimport`, `cxf-codegen`, `jaxws-maven-plugin` | `openapi-generator` |

**Los dos están escritos a mano**, y eso es deliberado:

- El **WSDL** es lo que tiene un servicio SOAP *legacy*: el contrato está congelado y el servidor
  se implementa contra él. Además, en este entorno `wsdl4j` no está disponible, así que Spring WS
  no puede *generar* el WSDL a partir de las anotaciones (sí puede *servir* uno escrito a mano).
- El **OpenAPI** está escrito a mano por simetría, y porque `springdoc-openapi` (que lo generaría
  desde el código) no es compatible con Spring Boot 4.1.2 en esta caché.

Los dos declaran **los mismos campos**: `sku`, `quantity`, `customerId` por un lado;
`sku`, `quantity`, `unitPrice`, `total` por otro. Esa coincidencia es lo que comprueba la suite.

---

## 3. Los dos stubs

Un cliente generado no lleva las URLs escritas a mano: **las saca del contrato**. Eso es lo que
hacen estos dos, y por eso la suite puede comprobar que cada uno va a donde dice su contrato.

### `QuoteRestClient` — estilo `openapi-generator`

```java
QuoteRestClient cliente = new QuoteRestClient(contractDocuments, "http://localhost:8080/api");
QuoteResponse respuesta = cliente.quote(new QuoteRequest("SKU-1", 3, "VIP-9"));
```

Lee `servers[0].url` y `paths./q2/rest/quotes` del OpenAPI para construir la llamada.

### `QuoteService` + `QuotePortType` + `QuoteSoapClient` — estilo `wsimport`

```java
QuotePortType port = new QuoteService().getQuotePort();
SoapQuoteResponse respuesta = port.quote(new SoapQuoteRequest("SKU-1", 3, "VIP-9"));
```

`QuoteService` hace de localizador: resuelve la dirección `soap:address` del WSDL.
`QuotePortType` es la interfaz del puerto, con un método por operación del `portType`.

Los dos stubs **no son beans**: se construyen con la URL base que se quiera, igual que un cliente
generado.

---

## 4. La equivalencia, demostrada

```bash
# El informe de la suite
curl -s -X POST http://localhost:8080/api/q2/verify | jq '{total, passed, failed}'
# → { "total": 10, "passed": 10, "failed": 0 }
```

Las diez comprobaciones:

| # | Comprobación |
|---|---|
| 1 | El WSDL declara la operación `quote` |
| 2 | El esquema del WSDL define los campos de la petición y de la respuesta |
| 3 | El OpenAPI declara la operación `quote` |
| 4 | **Los dos contratos describen la misma operación** |
| 5 | El stub SOAP construye un sobre SOAP válido |
| 6 | El stub REST invoca la URL que declara el OpenAPI |
| 7 | El `soap:address` del WSDL apunta al servlet SOAP |
| 8 | **Los dos caminos devuelven el mismo resultado** |
| 9 | El endpoint SOAP responde con un SOAP Fault ante una petición inválida |
| 10 | El endpoint REST responde 400 ante una petición inválida |

Las comprobaciones 4 y 8 son las que responden a la pregunta: los dos contratos son la misma
operación escrita en dos dialectos, y por eso se pueden generar clientes intercambiables.

---

## 5. Probarlo a mano

```bash
BASE=http://localhost:8080/api

# El contrato REST
curl -s $BASE/q2/openapi.yaml | head -20

# El contrato SOAP
curl -s "$BASE/q2/soap/quote-service.wsdl" | head -20

# La misma operación por REST
curl -s -X POST $BASE/q2/rest/quotes -H 'Content-Type: application/json' \
     -d '{"sku":"SKU-1","quantity":3,"customerId":"VIP-9"}'
# → {"sku":"SKU-1","quantity":3,"unitPrice":19.99,"total":53.97}

# Y por SOAP
curl -s -X POST $BASE/q2/soap/quote-service -H 'Content-Type: text/xml; charset=utf-8' -d '
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
    <ns2:quote xmlns:ns2="http://example.com/quote">
      <ns2:sku>SKU-1</ns2:sku>
      <ns2:quantity>3</ns2:quantity>
      <ns2:customerId>VIP-9</ns2:customerId>
    </ns2:quote>
  </soapenv:Body>
</soapenv:Envelope>'
# → <ns2:quoteResponse …><ns2:total>53.97</ns2:total></ns2:quoteResponse>
```

El mismo error, expresado en los dos dialectos:

```bash
# REST → 400 con el mensaje
curl -s -X POST $BASE/q2/rest/quotes -H 'Content-Type: application/json' \
     -d '{"sku":"NOPE","quantity":3,"customerId":"C-1"}'

# SOAP → SOAP Fault con el mismo mensaje (HTTP 500, que es lo que dicta SOAP)
curl -s -X POST $BASE/q2/soap/quote-service -H 'Content-Type: text/xml' -d '…'
# → <SOAP-ENV:Fault><faultcode>SOAP-ENV:Client</faultcode>
#   <faultstring>unknown sku: NOPE</faultstring></SOAP-ENV:Fault>
```

---

## 6. Generar los stubs de verdad

Los generadores no están en la caché de Maven de este entorno (requieren red), así que los stubs
están escritos a mano. Estos son los comandos exactos:

### REST, con `openapi-generator-maven-plugin`

```xml
<plugin>
  <groupId>org.openapitools</groupId>
  <artifactId>openapi-generator-maven-plugin</artifactId>
  <version>7.10.0</version>
  <executions>
    <execution>
      <goals><goal>generate</goal></goals>
      <configuration>
        <inputSpec>${project.basedir}/src/main/resources/q02/contract/openapi.yaml</inputSpec>
        <generatorName>java</generatorName>
        <apiPackage>com.ejemplo.api.client</apiPackage>
        <modelPackage>com.ejemplo.api.model</modelPackage>
        <invokerPackage>com.ejemplo.api.client</invokerPackage>
        <useJakartaEe>true</useJakartaEe>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Genera un modelo por esquema (`QuoteRequest`, `QuoteResponse`) y un `QuoteApi` con un método por
operación. Con `generatorName=okhttp-gson` sale además el cliente HTTP completo.

### SOAP, con `cxf-codegen-plugin` o `jaxws-maven-plugin`

```xml
<!-- cxf-codegen-plugin -->
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>cxf-codegen-plugin</artifactId>
  <version>3.5.7</version>
  <executions>
    <execution>
      <goals><goal>wsdl2java</goal></goals>
      <configuration>
        <wsdlOptions>
          <wsdlOption><wsdl>${project.basedir}/src/main/resources/q02/wsdl/quote-service.wsdl</wsdl></wsdlOption>
          <extraargs><extraarg>-client</extraarg></extraargs>
        </wsdlOptions>
      </configuration>
    </execution>
  </executions>
</plugin>
```

O, con la herramienta de línea de comandos del JDK (que ya no viene en el JDK 11+):

```bash
wsimport -keep -d target/generated-sources \
         -wsdlLocation http://localhost:8080/api/q2/soap/quote-service.wsdl \
         http://localhost:8080/api/q2/soap/quote-service.wsdl
```

Genera `QuoteService` (el localizador), `QuotePortType` (la interfaz del puerto) y las clases
JAXB de los payloads. **Son exactamente las tres piezas de este módulo**, escritas a mano.

---

## 7. REST frente a SOAP, sin“(casi)” theoretical

| | REST | SOAP |
|---|---|---|
| Contrato | OpenAPI (JSON/YAML) | WSDL + XSD |
| Formato | JSON, XML, texto, binario… | XML, siempre |
| Transporte | HTTP | HTTP, SMTP, JMS, MQ… |
| Semántica de error | código HTTP + cuerpo | `<SOAP-ENV:Fault>` en el sobre |
| Código en el cuerpo | casi nunca | casi siempre (`faultcode`) |
| Autodescripción | con OpenAPI | con el WSDL |
| Generación de stubs | `openapi-generator` | `wsimport`, `cxf-codegen` |
| Descubrimiento | OpenAPI/Swagger UI | WSDL, UDDI |
| Interoperabilidad | HTTP + JSON, muy widespread | XML estándar, más verboso |

Y lo que de verdad importa en una entrevista: **los dos son HTTP**. SOAP no es «otro protocolo»:
es HTTP con un sobre XML encima y un contrato que se puede generar. La diferencia práctica es
que el sobre XML te obliga a declarar el esquema, y por eso SOAP encaja con integraciones
enterprise donde el contrato tiene que ser firmable y versionado.

---

## 8. Los tres golpes de Spring WS 5 + Spring Boot 4

Esto es lo que más tiempo costó, y es justo lo que distingue a alguien que ha fought con SOAP
*legacy* de alguien que sólo ha leído sobre ello:

1. **`@PayloadMethod` ya no existe.** En Spring WS 5 la operación se declara con
   `@PayloadRoot(namespace, localPart)` y la respuesta con `@ResponsePayload`. Y la **clase** del
   endpoint tiene que llevar `@Endpoint` (un `@Component` especializado): el mapping de endpoints
   la localiza por esa anotación, no por ser un bean cualquiera.

2. **Ya no hay `WebServiceMessageConverter`.** El modelo es de *payload processors* y *argument
   resolvers*. El `DefaultMethodEndpointAdapter` trae unos por defecto que incluyen el de JAXB.

3. **Boot 4 aparta su auto-configuración de servicios web** en cuanto hay un
   `WsConfigurationSupport` en el contexto, que es justo lo que crea `@EnableWs`. Si usas
   `@EnableWs`, **tienes que registrar el servlet a mano**:

   ```java
   @Bean
   ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(WebApplicationContext context) {
       MessageDispatcherServlet servlet = new MessageDispatcherServlet(context);
       return new ServletRegistrationBean<>(servlet, "/q2/soap/*");
   }
   ```

   Y el WSDL se publica en `<ruta>/<nombre-del-bean>.wsdl`, no en el `?wsdl` clásico: el servlet
   busca beans `WsdlDefinition` **por nombre**, y publica el que encuentre bajo ese nombre.

---

## 9. Tests

```bash
./mvnw -o test
```

| Test | Qué cubre |
|---|---|
| `ContractDocumentsTest` | Los dos contratos se leen y se interpretan bien |
| `QuoteStubsTest` | Los dos stubs, contra el servidor real, y que devuelven lo mismo |
| `ContractVerificationServiceTest` | La suite sale 100 % en verde |

---

*Volver al [índice del examen](../README.md).*
