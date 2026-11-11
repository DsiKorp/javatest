package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.web;

import java.net.URI;
import java.util.List;

import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ContainsResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.CreateListRequest;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ElementRequest;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ElementResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.IndexOfResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ListResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ListSummary;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.service.ListService;

/**
 * API REST para crear y manipular instancias de las listas implementadas desde cero.
 *
 * <p>El tipo de implementación va en la URL ({@code /lists/arraylist/...} frente a
 * {@code /lists/linkedlist/...}) para que quede explícito en la propia ruta qué estructura
 * se está poniendo a prueba.</p>
 *
 * <p>El prefijo {@code /api/q1} no aparece en los {@code @RequestMapping}: lo aporta el
 * {@code server.servlet.context-path}, que el contenedor se quita antes de pasar la petición
 * al dispatcher. Así el mapeo del controlador queda limpio y las URLs reales llevan el prefijo
 * de la pregunta del examen, sin colisionar con las demás.</p>
 *
 * <p><strong>Ejemplo de uso con {@code curl}:</strong></p>
 * <pre>{@code
 * # Crear una lista de la implementación elegida
 * curl -X POST http://localhost:8080/api/q1/lists/arraylist \
 * -H 'Content-Type: application/json' \
 * -d '{"values":[1, "a", null]}'
 * # → 201 {"id":"9eff...","type":"ARRAY_LIST","size":3,"values":[1,"a",null]}
 *
 * ID=9eff4923-e21f-4333-87d3-d1e8d15e99e2
 *
 * # Consultar, mutar y buscar
 * curl http://localhost:8080/api/q1/lists/arraylist/$ID
 * curl -X POST http://localhost:8080/api/q1/lists/arraylist/$ID/elements \
 * -H 'Content-Type: application/json' -d '{"value":"nuevo"}'
 * curl -X POST http://localhost:8080/api/q1/lists/arraylist/$ID/elements/0 \
 * -H 'Content-Type: application/json' -d '{"value":"primero"}'
 * curl -X PUT http://localhost:8080/api/q1/lists/arraylist/$ID/elements/1 \
 * -H 'Content-Type: application/json' -d '{"value":"cambiado"}'
 * curl -X POST http://localhost:8080/api/q1/lists/arraylist/$ID/index-of \
 * -H 'Content-Type: application/json' -d '{"value":"cambiado"}'
 * }</pre>
 */
@RestController
@RequestMapping("/q1/lists")
@Validated
public class MyListController {

	private final ListService listService;

	/**
  * Crea el controlador con su servicio.
  *
  * @param listService el servicio que gestiona las listas
  */
	public MyListController(ListService listService) {
		this.listService = listService;
	}

	/**
  * Crea una lista nueva.
  *
  * @param type implementación a utilizar
  * @param request valores iniciales
  * @return la lista creada
  */
	@PostMapping("/{type}")
	public ResponseEntity<ListResponse> create(@PathVariable String type,
	@RequestBody(required = false) CreateListRequest request) {
		ListType listType = ListType.parse(type);
		List<Object> values = (request == null) ? null : request.values();
		ListResponse created = listService.create(listType, values);
		// fromCurrentContextPath() ya incluye el context-path configurado, así que la URL
		// publicada es correcta (/api/q1/lists/...) sin repetir el prefijo a mano.
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path("/q1/lists/{type}/{id}")
			.buildAndExpand(listType.name().toLowerCase(), created.id())
			.toUri();
		return ResponseEntity.created(location).body(created);
	}

	/**
  * Devuelve el resumen de las listas registradas, opcionalmente filtradas por tipo.
  *
  * @param type filtro opcional por implementación
  * @return el resumen de las listas registradas
  */
	@GetMapping
	public ResponseEntity<List<ListSummary>> findAll(@RequestParam(required = false) String type) {
		ListType filter = (type == null || type.isBlank()) ? null : ListType.parse(type);
		return ResponseEntity.ok(listService.findAll(filter));
	}

	/**
  * Devuelve el estado actual de una lista.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @return el estado de la lista
  */
	@GetMapping("/{type}/{id}")
	public ResponseEntity<ListResponse> find(@PathVariable String type, @PathVariable String id) {
		return ResponseEntity.ok(listService.find(ListType.parse(type), id));
	}

	/**
  * Elimina una lista del registro en memoria.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @return una respuesta {@code 204 No Content}
  */
	@DeleteMapping("/{type}/{id}")
	public ResponseEntity<Void> delete(@PathVariable String type, @PathVariable String id) {
		listService.delete(ListType.parse(type), id);
		return ResponseEntity.noContent().build();
	}

	/**
  * Añade un elemento al final de la lista.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param request valor a añadir
  * @return el estado de la lista tras la operación
  */
	@PostMapping("/{type}/{id}/elements")
	public ResponseEntity<ListResponse> add(@PathVariable String type, @PathVariable String id,
	@RequestBody ElementRequest request) {
		ListResponse response = listService.add(ListType.parse(type), id, request.value());
		return ResponseEntity.status(201).body(response);
	}

	/**
  * Añade un elemento en la posición indicada.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param index posición de inserción
  * @param request valor a añadir
  * @return el estado de la lista tras la operación
  */
	@PostMapping("/{type}/{id}/elements/{index}")
	public ResponseEntity<ListResponse> addAt(@PathVariable String type, @PathVariable String id,
	@PathVariable @PositiveOrZero int index, @RequestBody ElementRequest request) {
		ListResponse response = listService.addAt(ListType.parse(type), id, index, request.value());
		return ResponseEntity.status(201).body(response);
	}

	/**
  * Reemplaza el valor de la posición indicada.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param index posición a reemplazar
  * @param request nuevo valor
  * @return el estado de la lista tras la operación
  */
	@PutMapping("/{type}/{id}/elements/{index}")
	public ResponseEntity<ListResponse> set(@PathVariable String type, @PathVariable String id,
	@PathVariable @PositiveOrZero int index, @RequestBody ElementRequest request) {
		ListResponse response = listService.set(ListType.parse(type), id, index, request.value());
		return ResponseEntity.ok(response);
	}

	/**
  * Elimina el elemento de la posición indicada.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param index posición a eliminar
  * @return el estado de la lista tras la operación
  */
	@DeleteMapping("/{type}/{id}/elements/{index}")
	public ResponseEntity<ListResponse> removeAt(@PathVariable String type, @PathVariable String id,
	@PathVariable @PositiveOrZero int index) {
		ListResponse response = listService.removeAt(ListType.parse(type), id, index);
		return ResponseEntity.ok(response);
	}

	/**
  * Devuelve el valor almacenado en una posición.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param index posición consultada
  * @return el valor almacenado en esa posición
  */
	@GetMapping("/{type}/{id}/elements/{index}")
	public ResponseEntity<ElementResponse> getElement(@PathVariable String type, @PathVariable String id,
	@PathVariable @PositiveOrZero int index) {
		return ResponseEntity.ok(listService.getElement(ListType.parse(type), id, index));
	}

	/**
  * Devuelve la posición de la primera aparición de un valor.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param request valor buscado
  * @return la posición de la primera aparición
  */
	@PostMapping("/{type}/{id}/index-of")
	public ResponseEntity<IndexOfResponse> indexOf(@PathVariable String type, @PathVariable String id,
	@RequestBody ElementRequest request) {
		return ResponseEntity.ok(listService.indexOf(ListType.parse(type), id, request.value()));
	}

	/**
  * Devuelve si un valor está presente en la lista.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param request valor buscado
  * @return si el valor está presente
  */
	@PostMapping("/{type}/{id}/contains")
	public ResponseEntity<ContainsResponse> contains(@PathVariable String type, @PathVariable String id,
	@RequestBody ElementRequest request) {
		return ResponseEntity.ok(listService.contains(ListType.parse(type), id, request.value()));
	}
}
