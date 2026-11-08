# ArrayList vs LinkedList — implementación desde cero + API REST

Prueba técnica: implementar `ArrayList` y `LinkedList` **desde cero**, sin apoyarse en las
colecciones de `java.util`, y exponer una API HTTP para poder ejercitar (y verificar) el
resultado sin necesidad de leer el código.

- **Java 25** · **Spring Boot 4.1.2** · **Maven**
- `MyArrayList` y `MyLinkedList` implementan el contrato completo de `java.util.List`
- 52 comprobaciones de autoverificación ejecutables por HTTP: `POST /api/q1/verify`

---

## 1. La regla de oro

> **Ninguna clase del paquete `q01/arraylistvslinkedlist/collections/` puede usar las colecciones de `java.util` para
> resolver su comportamiento.**

### Prohibido en `src/main/java/.../collections/`

| Prohibido | Por qué |
|---|---|
| `java.util.ArrayList` | la implementación objeto de la prueba |
| `java.util.LinkedList` | la implementación objeto de la prueba |
| `java.util.Collections` | utilidades de colección (`sort`, `unmodifiableList`, …) |
| `java.util.Arrays` | utilidades de array (`copyOf`, `sort`, `asList`, `fill`) |
| `java.util.HashMap` y similares | tablas hash de `java.util` |
| `List.of(...)`, `Arrays.copyOf(...)`, `Arrays.sort(...)` | atajos equivalentes a lo anterior |

### Permitido en `q01/arraylistvslinkedlist/collections/`

- **Interfaces**: `java.util.List`, `Collection`, `Iterator`, `ListIterator`, `Deque`,
  `RandomAccess`, `Comparator`. Implementar las interfaces del JDK está permitido (lo que no se
  permite es delegar en sus implementaciones).
- **Excepciones**: `NoSuchElementException`, `IndexOutOfBoundsException`,
  `ConcurrentModificationException`.
- **Utilidades de soporte**: `java.util.Objects` (equals/hashCode), `java.util.Spliterators`,
  `java.lang.reflect.Array`, `System.arraycopy`, `StringBuilder`.

Fuera del paquete `q01/arraylistvslinkedlist/collections/` (capa web, servicio y DTOs) el uso de `java.util` es normal y
necesario: Spring, Jackson y el registro en memoria lo requieren. La frontera está documentada en
`q01/arraylistvslinkedlist/collections/package-info.java` y es verificable:

```bash
# Los únicos imports del paquete deben ser interfaces y utilidades permitidas
grep -rh "^import" src/main/java/com/dsikorp/epam/javatest/q01/arraylistvslinkedlist/collections/ | sort -u
```

Los imports reales del paquete son sólo estos: `java.util.Collection`, `Comparator`,
`ConcurrentModificationException`, `Deque`, `Iterator`, `List`, `ListIterator`, `Locale`,
`NoSuchElementException`, `Objects`, `RandomAccess`, `Spliterator`, `Spliterators`,
`java.lang.reflect.Array` y las tres functional interfaces de `java.util.function`.

---

## 2. Estructura

```
src/main/java/com/dsikorp/epam/javatest/q01/arraylistvslinkedlist/
├── collections/                     ← el núcleo, sin java.util
│   ├── MyList.java                  interface MyList<E> extends java.util.List<E>
│   ├── AbstractMyList.java          algoritmos comunes (equals, hashCode, indexOf, toArray, sort…)
│   ├── MyArrayList.java             Object[] redimensionable
│   ├── MyLinkedList.java            cadena de nodos prev/next + API de Deque
│   ├── MyListIterator.java          ListIterator fail-fast
│   ├── MySubList.java               vista de subList
│   └── ListType.java                enum ARRAY_LIST | LINKED_LIST + fábrica
├── dto/                             records de entrada y salida
├── exception/                       errores de dominio
├── service/                         ListService (registro en memoria) y VerificationService
├── verification/                    batería de comprobaciones del contrato
└── web/                             controladores y manejo de errores
```

### Cómo está repartido el trabajo (igual que en el JDK)

`AbstractMyList` implementa **una sola vez** todo lo que sólo depende de
`get`/`set`/`add`/`remove`: `indexOf`, `lastIndexOf`, `contains`, `containsAll`, `toArray`,
`toString`, `equals`, `hashCode`, `addAll`, `removeAll`, `retainAll`, `forEach`, `removeIf`,
`replaceAll` y `sort`.

Cada subclase sólo aporta las seis primitivas: `size()`, `get(int)`, `set(int, E)`,
`add(int, E)`, `remove(int)` y `listIterator(int)`.

---

## 3. Cómo ejecutarlo

```bash
./mvnw -o clean verify      # compila y pasa toda la suite de tests
./mvnw -o spring-boot:run   # arranca en http://localhost:8080
```

---

## 4. La prueba en un click

```bash
curl -s -X POST http://localhost:8080/api/q1/verify | jq '{total, passed, failed, durationMs}'
```

```json
{ "total": 52, "passed": 52, "failed": 0, "durationMs": 7 }
```

El detalle de cada comprobación:

```bash
curl -s http://localhost:8080/api/q1/verify | jq '.checks[] | select(.passed == false)'
```

El informe nunca falla: recoge el resultado de cada comprobación para que se vea exactamente qué
pasa y qué no.

---

## 5. API REST

El tipo de implementación va **en la URL**, para que quede explícito qué estructura se está
poniendo a prueba.

Todas las rutas de la tabla cuelgan del context-path `/api/q1`, configurado en
`application.properties` con `server.servlet.context-path`. Tomcat le quita ese prefijo a la
petición antes de pasársela al dispatcher, así que los `@RequestMapping` de los controladores son
`/lists` y `/verify`, **sin** el prefijo. La URL pública sí lo lleva.

| Método | Ruta | Body | Respuesta |
|---|---|---|---|
| `POST` | `/api/q1/lists/{type}` | `{"values":[1,"a",null]}` | `201` lista creada |
| `GET` | `/api/q1/lists` | `?type=ARRAY_LIST` (opcional) | `200` resumen de las listas |
| `GET` | `/api/q1/lists/{type}/{id}` | — | `200` estado de la lista |
| `DELETE` | `/api/q1/lists/{type}/{id}` | — | `204` |
| `POST` | `/api/q1/lists/{type}/{id}/elements` | `{"value":x}` | `201` añade al final |
| `POST` | `/api/q1/lists/{type}/{id}/elements/{index}` | `{"value":x}` | `201` añade en posición |
| `PUT` | `/api/q1/lists/{type}/{id}/elements/{index}` | `{"value":x}` | `200` reemplaza |
| `DELETE` | `/api/q1/lists/{type}/{id}/elements/{index}` | — | `200` elimina por posición |
| `GET` | `/api/q1/lists/{type}/{id}/elements/{index}` | — | `200` un elemento |
| `POST` | `/api/q1/lists/{type}/{id}/index-of` | `{"value":x}` | `200` `{"index":n,"found":b}` |
| `POST` | `/api/q1/lists/{type}/{id}/contains` | `{"value":x}` | `200` `{"contains":b}` |
| `GET`/`POST` | `/api/q1/verify` | — | `200` informe de verificación |

`{type}` acepta `arraylist`, `array-list`, `array_list` y `linkedlist`, sin distinguir
mayúsculas.

### Recorrido completo con `curl`

```bash
BASE=http://localhost:8080/api/q1/lists

# Crear las dos listas con el mismo contenido
curl -s -X POST $BASE/arraylist  -H 'Content-Type: application/json' -d '{"values":[1,2,3]}'
curl -s -X POST $BASE/linkedlist -H 'Content-Type: application/json' -d '{"values":[1,2,3]}'

# Operar sobre una de ellas
ID=<el id que devuelve la respuesta>
curl -s $BASE/arraylist/$ID | jq
curl -s -X POST $BASE/arraylist/$ID/elements -H 'Content-Type: application/json' -d '{"value":"nuevo"}' | jq
curl -s -X POST $BASE/arraylist/$ID/index-of  -H 'Content-Type: application/json' -d '{"value":2}' | jq
curl -s -X DELETE $BASE/arraylist/$ID/elements/0 | jq
```

### Errores

Todos los errores salen con el mismo shape:

```json
{
  "timestamp": "2026-11-08T01:06:34.03Z",
  "status": 404,
  "error": "Not Found",
  "message": "No ARRAY_LIST list found with id 'no-existe'",
  "path": "/api/q1/lists/arraylist/no-existe",
  "details": []
}
```

| Situación | Status |
|---|---|
| Lista inexistente (o de otro tipo) | `404` |
| Tipo de lista desconocido | `400` |
| Índice negativo | `400` |
| Índice fuera de rango | `400` |
| Cuerpo de la petición malformado | `400` |

---

## 6. `MyArrayList` vs `MyLinkedList`

| | `MyArrayList` | `MyLinkedList` |
|---|---|---|
| Estructura | `Object[]` redimensionable | nodos con `next`/`prev` |
| Capacidad inicial | 10, crece ×1.5 | no aplica |
| Acceso por posición | O(1) | O(n) (recorre desde el extremo más cercano) |
| Inserción al final | O(1) amortizado | O(1) |
| Inserción en posición | O(n) (desplaza con `arraycopy`) | O(n) (busca el nodo) |
| Borrado por posición | O(n) | O(n) (busca el nodo) |
| Borrado por valor | O(n) | O(n) |
| Buscar un valor | O(n) | O(n) |
| Recorrido | O(n), con acceso contiguo en memoria | O(n), saltando entre nodos |
| En memoria | contiguo | disperso (cada nodo es un objeto) |
| `RandomAccess` | sí | no |
| `Deque` (cola/pila) | no | sí |

**Cuándo usar cada una:** `MyArrayList` cuando el acceso aleatorio y la cache de CPU importan
(lectura dominante). `MyLinkedList` cuando se inserta o se borra mucho en los extremos, se
necesita una pila o una cola FIFO, o se recorre con `ListIterator` en ambos sentidos.

---

## 7. Decisiones de diseño

- **`equals` y `hashCode` cruzados entre implementaciones.** `equals` compara elemento a
  elemento contra *cualquier* `java.util.List`, así que una `MyArrayList` y una `MyLinkedList`
  con los mismos elementos son iguales entre sí — igual que `ArrayList.equals(LinkedList)` en el
  JDK. `hashCode` usa el algoritmo 31-based del JDK, de modo que dos listas iguales producen el
  mismo hash aunque sean de implementaciones distintas.
- **`sort` propio.** El método `default` de `List.sort` delega en `Arrays.sort`, que está
  prohibido. `AbstractMyList` lo sobreescribe con un *merge sort* propio y estable.
- **Iteradores fail-fast.** `modCount` se incrementa en cada modificación estructural; si el
  iterador detecta que ha cambiado, lanza `ConcurrentModificationException`. `iterator.remove()` y
  `iterator.add()` actualizan el contador para que la propia mutación no dispare la excepción.
- **`subList` como vista.** `MySubList` no copia: guarda la lista raíz, el desplazamiento
  absoluto y el tamaño, y delega en la raíz. Eso permite anidar subList sin casos especiales.
  Una modificación de la raíz *desde fuera* deja la vista obsoleta y la siguiente operación lanza
  `ConcurrentModificationException`, igual que en el JDK.
- **`MyLinkedList` implementa `Deque`.** Es la diferencia estructural que menciona el enunciado
  de la pregunta: `LinkedList` no es sólo una lista, también es una cola FIFO y una pila LIFO.
- **`reversed()` devuelve una copia, no una vista.** Desviación consciente respecto al JDK
  (donde `List.reversed()` sí es una vista): `java.util.List` y `java.util.Deque` declaran
  `reversed()` con el mismo nombre pero tipos de retorno distintos, y `MyLinkedList` implementa
  las dos interfaces. El comportamiento es idéntico en las dos implementaciones propias.
- **Las listas viven en memoria**, en un `ConcurrentHashMap` del `ListService`. Es una
  herramienta de demostración y entrevista, no un servicio de producción.

---

## 8. Tests

```bash
./mvnw -o test
```

| Test | Qué cubre |
|---|---|
| `MyListContractTest` | 25 comprobaciones del contrato de `List` ejecutadas contra **las dos** implementaciones (50 tests) |
| `MyListMatchesJdkTest` | compara paso a paso, con una semilla fija, el comportamiento propio frente a `java.util.ArrayList` y `java.util.LinkedList` (500 operaciones aleatorias) |
| `MyArrayListTest` | capacidad inicial, crecimiento ×1.5, supervivencia al churn |
| `MyLinkedListTest` | coherencia de los extremos, FIFO, LIFO, iterador bidireccional |
| `VerificationServiceTest` | el informe de la suite sale 100 % en verde |
| `MyListControllerTest` | contrato HTTP: rutas, códigos de estado y forma del JSON |

Todos viven en el paquete espejo del punto:
`src/test/java/com/dsikorp/epam/javatest/q01/arraylistvslinkedlist/...`

El test más fuerte es `MyListMatchesJdkTest`: aplica la misma secuencia de operaciones sobre la
implementación propia y sobre la del JDK, y después de **cada** operación compara tamaño,
contenido, array, hash e igualdad. Las implementaciones propias no "se parecen" al JDK: se
comportan como el JDK.

---

*Volver al [índice del examen](../README.md).*
