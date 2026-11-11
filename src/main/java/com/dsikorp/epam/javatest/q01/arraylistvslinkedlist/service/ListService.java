package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.ListType;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.collections.MyList;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ContainsResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ElementResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.IndexOfResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ListResponse;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.dto.ListSummary;
import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.exception.ListNotFoundException;

/**
 * Servicio que gestiona las instancias de listas creadas a través de la API.
 *
 * <p>Las listas viven en memoria: es una herramienta de demostración y entrevista, no un
 * servicio de producción. El registro es un {@link ConcurrentHashMap} para que las peticiones
 * concurrentes no se pisen.</p>
 *
 * <p><strong>Ejemplo:</strong></p>
 * <pre>{@code
 * ListService servicio = new ListService();
 *
 * // Crear una lista de la implementación elegida
 * ListResponse creada = servicio.create(ListType.ARRAY_LIST, List.of(1, 2, 3));
 * String id = creada.id();
 *
 * // Operar sobre ella
 * servicio.add(ListType.ARRAY_LIST, id, "nuevo");
 * servicio.addAt(ListType.ARRAY_LIST, id, 0, "primero");
 * servicio.set(ListType.ARRAY_LIST, id, 1, "cambiado");
 *
 * IndexOfResponse posicion = servicio.indexOf(ListType.ARRAY_LIST, id, "cambiado");
 * posicion.found();   // true
 * posicion.index();    // 1
 *
 * // Pedir una lista que no existe, o de otro tipo, lanza ListNotFoundException
 * servicio.find(ListType.LINKED_LIST, id);   // → ListNotFoundException
 * }</pre>
 */
@Service
public class ListService {

	/**
  * Crea el servicio con el registro de listas vacío.
  */
	public ListService() {
	}

	/** Una lista viva y el tipo de implementación que la respalda. */
	private record StoredList(ListType type, MyList<Object> list) {
	}

	private final Map<String, StoredList> lists = new ConcurrentHashMap<>();

	/**
  * Crea una lista nueva con los valores indicados.
  *
  * @param type implementación a utilizar
  * @param values valores iniciales; {@code null} equivale a una lista vacía
  * @return el estado de la lista recién creada
  */
	public ListResponse create(ListType type, List<Object> values) {
		MyList<Object> list = type.create();
		if (values != null) {
			for (Object value : values) {
				list.add(value);
			}
		}
		String id = UUID.randomUUID().toString();
		lists.put(id, new StoredList(type, list));
		return toResponse(id, type, list);
	}

	/**
  * Devuelve el resumen de las listas registradas.
  *
  * @param type filtro opcional por implementación
  * @return el resumen de todas las listas registradas
  */
	public List<ListSummary> findAll(ListType type) {
		List<ListSummary> summaries = new ArrayList<>();
		for (Map.Entry<String, StoredList> entry : lists.entrySet()) {
			StoredList stored = entry.getValue();
			if (type == null || stored.type() == type) {
				summaries.add(new ListSummary(entry.getKey(), stored.type(), stored.list().size()));
			}
		}
		return summaries;
	}

	/**
  * Devuelve el estado actual de una lista.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @return el estado de la lista
  * @throws ListNotFoundException si no existe esa lista
  */
	public ListResponse find(ListType type, String id) {
		StoredList stored = require(type, id);
		return toResponse(id, stored.type(), stored.list());
	}

	/**
  * Elimina una lista del registro en memoria.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @throws ListNotFoundException si no existe esa lista
  */
	public void delete(ListType type, String id) {
		require(type, id);
		lists.remove(id);
	}

	/**
  * Añade un elemento al final de la lista.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param value valor a añadir
  * @return el estado de la lista tras la operación
  */
	public ListResponse add(ListType type, String id, Object value) {
		StoredList stored = require(type, id);
		stored.list().add(value);
		return toResponse(id, stored.type(), stored.list());
	}

	/**
  * Añade un elemento en la posición indicada.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param index posición de inserción
  * @param value valor a añadir
  * @return el estado de la lista tras la operación
  */
	public ListResponse addAt(ListType type, String id, int index, Object value) {
		StoredList stored = require(type, id);
		stored.list().add(index, value);
		return toResponse(id, stored.type(), stored.list());
	}

	/**
  * Reemplaza el valor de la posición indicada.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param index posición a reemplazar
  * @param value nuevo valor
  * @return el estado de la lista tras la operación
  */
	public ListResponse set(ListType type, String id, int index, Object value) {
		StoredList stored = require(type, id);
		stored.list().set(index, value);
		return toResponse(id, stored.type(), stored.list());
	}

	/**
  * Elimina el elemento de la posición indicada.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param index posición a eliminar
  * @return el estado de la lista tras la operación
  */
	public ListResponse removeAt(ListType type, String id, int index) {
		StoredList stored = require(type, id);
		stored.list().remove(index);
		return toResponse(id, stored.type(), stored.list());
	}

	/**
  * Consulta el valor almacenado en una posición.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param index posición consultada
  * @return el valor almacenado en esa posición
  */
	public ElementResponse getElement(ListType type, String id, int index) {
		StoredList stored = require(type, id);
		return new ElementResponse(stored.list().get(index));
	}

	/**
  * Busca la primera aparición de un valor.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param value valor buscado
  * @return la posición de la primera aparición y si se ha encontrado
  */
	public IndexOfResponse indexOf(ListType type, String id, Object value) {
		StoredList stored = require(type, id);
		int index = stored.list().indexOf(value);
		return new IndexOfResponse(index, index >= 0);
	}

	/**
  * Comprueba si un valor está presente en la lista.
  *
  * @param type implementación esperada
  * @param id identificador de la lista
  * @param value valor buscado
  * @return si el valor está presente en la lista
  */
	public ContainsResponse contains(ListType type, String id, Object value) {
		StoredList stored = require(type, id);
		return new ContainsResponse(stored.list().contains(value));
	}

	private StoredList require(ListType type, String id) {
		StoredList stored = lists.get(id);
		if (stored == null || stored.type() != type) {
			throw new ListNotFoundException(id, type);
		}
		return stored;
	}

	private ListResponse toResponse(String id, ListType type, MyList<Object> list) {
		return new ListResponse(id, type, list.size(), new ArrayList<>(list));
	}
}
