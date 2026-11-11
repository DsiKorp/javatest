package com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dsikorp.epam.javatest.q01.arraylistvslinkedlist.service.ListService;
import com.jayway.jsonpath.JsonPath;

/**
 * Tests del slice web: comprueban el contrato HTTP (rutas, códigos de estado y forma del JSON)
 * sin tocar la implementación de las listas, que ya tiene sus propios tests.
 *
 * <p><strong>Sobre las rutas.</strong> Aquí no aparece el prefijo {@code /api/q1}: MockMvc
 * manda la petición directamente al dispatcher, igual que haría el contenedor, que ya le ha
 * quitado el context-path antes. El mapeo del controlador es {@code /lists}, y a ese es al que
 * responde el dispatcher. Que el prefijo funcione de verdad en las URLs públicas lo comprueba
 * {@link ContextPathIntegrationTest}.</p>
 */
@WebMvcTest(MyListController.class)
@Import(ListService.class)
class MyListControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private String createList(String type, String body) throws Exception {
		String response = mockMvc.perform(post("/q1/lists/" + type).contentType(MediaType.APPLICATION_JSON)
			.content(body))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();
		return JsonPath.read(response, "$.id");
	}

	@Test
	@DisplayName("crea una lista y devuelve su estado")
	void createsList() throws Exception {
		mockMvc.perform(post("/q1/lists/arraylist").contentType(MediaType.APPLICATION_JSON)
			.content("{\"values\":[1,\"a\",null]}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.type").value("ARRAY_LIST"))
			.andExpect(jsonPath("$.size").value(3))
			.andExpect(jsonPath("$.values[0]").value(1))
			.andExpect(jsonPath("$.values[1]").value("a"))
			.andExpect(jsonPath("$.values[2]").value(org.hamcrest.Matchers.nullValue()));
	}

	@Test
	@DisplayName("acepta los dos tipos de lista, con y sin guiones")
	void acceptsBothTypes() throws Exception {
		mockMvc.perform(post("/q1/lists/arraylist").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.type").value("ARRAY_LIST"));
		mockMvc.perform(post("/q1/lists/linked-list").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.type").value("LINKED_LIST"));
	}

	@Test
	@DisplayName("añade, reemplaza, consulta y elimina elementos")
	void mutatesElements() throws Exception {
		String id = createList("arraylist", "{\"values\":[\"a\",\"b\",\"c\"]}");

		mockMvc.perform(post("/q1/lists/arraylist/" + id + "/elements").contentType(MediaType.APPLICATION_JSON)
			.content("{\"value\":\"d\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.size").value(4))
			.andExpect(jsonPath("$.values[3]").value("d"));

		mockMvc.perform(post("/q1/lists/arraylist/" + id + "/elements/0")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"value\":\"z\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.values[0]").value("z"));

		mockMvc.perform(put("/q1/lists/arraylist/" + id + "/elements/1").contentType(MediaType.APPLICATION_JSON)
			.content("{\"value\":\"y\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.values[1]").value("y"));

		mockMvc.perform(get("/q1/lists/arraylist/" + id + "/elements/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.value").value("y"));

		mockMvc.perform(delete("/q1/lists/arraylist/" + id + "/elements/0"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size").value(4))
			.andExpect(jsonPath("$.values[0]").value("y"));
	}

	@Test
	@DisplayName("busca elementos por valor")
	void searchesElements() throws Exception {
		String id = createList("linkedlist", "{\"values\":[\"a\",\"b\",\"a\"]}");

		mockMvc.perform(post("/q1/lists/linkedlist/" + id + "/index-of").contentType(MediaType.APPLICATION_JSON)
			.content("{\"value\":\"a\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.index").value(0))
			.andExpect(jsonPath("$.found").value(true));

		mockMvc.perform(post("/q1/lists/linkedlist/" + id + "/contains").contentType(MediaType.APPLICATION_JSON)
			.content("{\"value\":\"zzz\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.contains").value(false));
	}

	@Test
	@DisplayName("lista las listas registradas y filtra por tipo")
	void listsRegisteredLists() throws Exception {
		String id = createList("arraylist", "{\"values\":[1]}");

		mockMvc.perform(get("/q1/lists"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[*].id", org.hamcrest.Matchers.hasItem(id)));

		mockMvc.perform(get("/q1/lists").param("type", "array_list"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[*].id", org.hamcrest.Matchers.hasItem(id)));
	}

	@Test
	@DisplayName("borra una lista existente")
	void deletesList() throws Exception {
		String id = createList("arraylist", "{\"values\":[1]}");

		mockMvc.perform(delete("/q1/lists/arraylist/" + id)).andExpect(status().isNoContent());
		mockMvc.perform(get("/q1/lists/arraylist/" + id)).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("responde 404 si la lista no existe")
	void returnsNotFound() throws Exception {
		mockMvc.perform(get("/q1/lists/arraylist/no-existe"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.path").value("/q1/lists/arraylist/no-existe"));
	}

	@Test
	@DisplayName("responde 404 si la lista existe pero es de otro tipo")
	void returnsNotFoundForWrongType() throws Exception {
		String id = createList("arraylist", "{\"values\":[1]}");

		mockMvc.perform(get("/q1/lists/linkedlist/" + id)).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("responde 400 si el tipo de lista no existe")
	void returnsBadRequestForUnknownType() throws Exception {
		mockMvc.perform(post("/q1/lists/vector").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("vector")));
	}

	@Test
	@DisplayName("responde 400 si el índice es negativo")
	void returnsBadRequestForNegativeIndex() throws Exception {
		String id = createList("arraylist", "{\"values\":[1]}");

		mockMvc.perform(get("/q1/lists/arraylist/" + id + "/elements/-1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	@DisplayName("responde 400 si el índice está fuera de rango")
	void returnsBadRequestForOutOfRangeIndex() throws Exception {
		String id = createList("arraylist", "{\"values\":[1]}");

		mockMvc.perform(get("/q1/lists/arraylist/" + id + "/elements/99"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	@DisplayName("responde 400 si el cuerpo de la petición está malformado")
	void returnsBadRequestForMalformedBody() throws Exception {
		String id = createList("arraylist", "{\"values\":[1]}");

		mockMvc.perform(post("/q1/lists/arraylist/" + id + "/elements").contentType(MediaType.APPLICATION_JSON)
			.content("{roto"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("el id de la lista se genera en el servidor")
	void generatesServerSideId() throws Exception {
		String response = mockMvc.perform(post("/q1/lists/arraylist").contentType(MediaType.APPLICATION_JSON)
			.content("{}"))
			.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).contains("\"id\":").contains("\"size\":0");
	}
}
