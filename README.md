# Preparación de la prueba técnica Java

Banco de pruebas para el listado de preguntas de entrevista senior de Java (EPAM). Cada punto del
examen se resuelve como **un módulo autónomo**: su propia implementación, sus propios tests, su
propio endpoint de autoverificación y su propia documentación.

- **Java 25** · **Spring Boot 4.1.2** · **Maven**

> **Guía en HTML:** [`docs/guia.html`](docs/guia.html) — el índice completo de los dos módulos,
> con el orden de lectura y las decisiones de diseño.
> [`docs/q01-anatomia.html`](docs/q01-anatomia.html) — profundización del punto 1.

---

## Puntos implementados

| # | Pregunta | Paquete | Verificación | Documentación |
|---|---|---|---|---|
| 01 | ArrayList vs LinkedList | `q01.arraylistvslinkedlist` | `POST /api/q1/verify` | [docs/q01-arraylist-vs-linkedlist.md](docs/q01-arraylist-vs-linkedlist.md) |
| 02 | REST frente a SOAP (WSDL y OpenAPI) | `q02.restvssoap` | `POST /api/q2/verify` | [docs/q02-rest-vs-soap.md](docs/q02-rest-vs-soap.md) |

---

## Cómo ejecutarlo

```bash
./mvnw -o clean verify      # compila y pasa toda la suite de tests
./mvnw -o spring-boot:run   # arranca en http://localhost:8080
```

La forma más rápida de ver el resultado del punto 1:

```bash
curl -s -X POST http://localhost:8080/api/q1/verify | jq '{total, passed, failed, durationMs}'
```

```json
{ "total": 52, "passed": 52, "failed": 0, "durationMs": 15 }
```

> Si tienes la aplicación arrancada desde IntelliJ y también la levantas por terminal, la segunda
> fallará con «port 8080 already in use». Usa otro puerto:
> `./mvnw -o spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`

---

## Prefijo de las URLs (`context-path`)

Todas las URLs cuelgan de un único prefijo, configurado en `application.properties`:

```properties
server.servlet.context-path=/api/q1
```

| | |
|---|---|
| URL pública | `http://localhost:8080/api/q1/lists/arraylist/{id}` |
| `@RequestMapping` del controlador | `/lists` — **sin** el prefijo |

El contenedor (Tomcat) le quita el context-path a la petición **antes** de pasársela al
dispatcher de Spring, así que el mapeo del controlador nunca lo lleva. Por eso los
`@RequestMapping` son `/lists` y `/verify`, y las URLs reales sí llevan `/api/q1`.

Ventaja: el prefijo se cambia en un único sitio, y cada pregunta del examen puede tener el
 suyo (`/api/q1`, `/api/q2`, …) sin tocar los controladores.

> En los tests con MockMvc las rutas van **sin** el prefijo, porque MockMvc manda la petición
> directamente al dispatcher. El test `ContextPathIntegrationTest` sí levanta un Tomcat real y
> comprueba que la URL pública lleva el prefijo.

---

## Configuración local con `.env`

La aplicación importa un fichero `.env` de la raíz del proyecto para sobrescribir la
configuración sin tocar el repositorio:

```properties
# src/main/resources/application.properties
spring.config.import=optional:file:.env[.properties]
```

Cópia el ejemplo y ajústalo:

```bash
cp .env.example .env
```

```properties
# .env  —  formato properties: clave=valor, sin comillas ni "export"
server.port=8080
#spring.profiles.active=local
#logging.level.com.dsikorp.epam.javatest=DEBUG
```

Tres detalles que importan:

| Detalle | Por qué |
|---|---|
| `optional:` | Si el fichero no existe, la aplicación **arranca igual**. Sin el prefijo, Spring aborta con `Config data resource 'file [.env]' ... does not exist`. |
| `[.properties]` | Indica cómo interpretar el fichero importado. Las alternativas son `[.yaml]` y `[.yml]`. |
| Claves planas | En un `.properties` **no** se escribe anidado como en YAML (`spring:` / `config:` / `import:`): todo va en una sola clave `spring.config.import`. |

Lo que declare en `.env` **sobrescribe** a `application.properties`, y `.env` está en el
`.gitignore` para que nunca se versione.

---

## Convención de organization

Cada pregunta del examen vive aislada bajo su propio paquete, con la misma forma interna:

```
src/main/java/com/dsikorp/epam/javatest/
├── JavatestApplication.java
└── qNN/
    └── <tema>/                    ← nombre del tema, en minúsculas y sin separadores
        ├── collections/            ← la implementación propia (el núcleo del ejercicio)
        ├── dto/                    ← records de entrada y salida
        ├── exception/              ← errores de dominio
        ├── service/                ← lógica de aplicación
        ├── verification/           ← batería de comprobaciones del contrato
        └── web/                    ← controladores y manejo de errores
```

Los tests van en el paquete espejo:

```
src/test/java/com/dsikorp/epam/javatest/qNN/<tema>/...
```

**Reglas:**

| Regla | Por qué |
|---|---|
| El paquete se llama `qNN` + el tema | `q01.arraylistvslinkedlist`, `q02.concurrencia`… Ordena solo en el árbol de ficheros y evita colisiones de nombres entre preguntas. |
| Cada pregunta tiene su propio espacio de URLs, `/api/qNN/...` | Spring falla al arrancar si dos controladores mapean la misma ruta. Prefijar por pregunta evita el conflicto de raíz. |
| Cada `@RestControllerAdvice` se acota con `basePackages` | Que el manejo de errores de la pregunta #2 no capture las excepciones de la #1. |
| La documentación de cada punto va en `docs/qNN-<tema>.md` | Este README es sólo el índice. |

**Código compartido:** si dos preguntas necesitan lo mismo, se extrae a un paquete `common/` en la
raíz. Hasta que ocurra, no se crea: es preferible duplicar un poco antes que acoplar preguntas
que no tienen por qué conocerse.

Ya ha ocurrido dos veces, y lo que hay en `common/` es exactamente el marco de verificación
(framework de checks, informe y `ErrorResponse`), que es genérico y no pertenece a ninguna
pregunta:

```
common/
├── dto/ErrorResponse.java
└── verification/          Check · CheckAction · CheckResult · VerificationReport ·
                            VerificationRunner · Verifications
```

**Y un aviso que costó sangre:** Spring deriva el **nombre del bean** del nombre de la clase, así
que dos preguntas no pueden tener clases con el mismo nombre simple. Por eso la pregunta 2 llama
a su servicio `ContractVerificationService` y no `VerificationService`.

---

## El patrón que sigue cada pregunta

1. **Implementación propia** en `collections/`, sin apoyarse en las colecciones de `java.util` (o
   en el mecanismo que la pregunta prohiba).
2. **Suite de verificación** en `verification/`: una batería de comprobaciones del contrato, que
   se ejecuta contra la implementación y devuelve un informe JSON con el detalle de cada caso.
3. **Endpoint HTTP** para que quien evalúa pueda comprobar el comportamiento sin leer el código.
4. **Tests que comparan contra el JDK** cuando la pregunta tenga un equivalente en la biblioteca
   estándar: es la evidencia más fuerte que se puede dar.
5. **Documentación** en `docs/`, con la tabla comparativa y las decisiones de diseño.
