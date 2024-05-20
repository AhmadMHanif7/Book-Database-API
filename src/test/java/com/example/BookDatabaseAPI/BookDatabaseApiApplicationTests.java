package com.example.BookDatabaseAPI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BookstoreApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		bookRepository.deleteAll();
	}

	@Test
	void shouldCreateBook() throws Exception {
		Book book = new Book();
		book.setTitle("Test Book");
		book.setAuthor("Test Author");
		book.setPublicationYear(2023);

		mockMvc.perform(post("/api/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(book)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.title").value("Test Book"))
				.andExpect(jsonPath("$.author").value("Test Author"))
				.andExpect(jsonPath("$.publicationYear").value(2023));
	}

	@Test
	void shouldGetBookById() throws Exception {
		Book book = new Book();
		book.setTitle("Test Book");
		book.setAuthor("Test Author");
		book.setPublicationYear(2023);
		Book savedBook = bookRepository.save(book);

		mockMvc.perform(get("/api/books/" + savedBook.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedBook.getId()))
				.andExpect(jsonPath("$.title").value("Test Book"))
				.andExpect(jsonPath("$.author").value("Test Author"))
				.andExpect(jsonPath("$.publicationYear").value(2023));
	}

	@Test
	void shouldUpdateBook() throws Exception {
		Book book = new Book();
		book.setTitle("Test Book");
		book.setAuthor("Test Author");
		book.setPublicationYear(2023);
		Book savedBook = bookRepository.save(book);

		Book updatedBook = new Book();
		updatedBook.setTitle("Updated Book");
		updatedBook.setAuthor("Updated Author");
		updatedBook.setPublicationYear(2024);

		mockMvc.perform(put("/api/books/" + savedBook.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedBook)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedBook.getId()))
				.andExpect(jsonPath("$.title").value("Updated Book"))
				.andExpect(jsonPath("$.author").value("Updated Author"))
				.andExpect(jsonPath("$.publicationYear").value(2024));
	}

	@Test
	void shouldDeleteBook() throws Exception {
		Book book = new Book();
		book.setTitle("Test Book");
		book.setAuthor("Test Author");
		book.setPublicationYear(2023);
		Book savedBook = bookRepository.save(book);

		mockMvc.perform(delete("/api/books/" + savedBook.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
	}

	@Test
	void shouldGetAllBooks() throws Exception {
		Book book1 = new Book();
		book1.setTitle("Test Book 1");
		book1.setAuthor("Test Author 1");
		book1.setPublicationYear(2023);

		Book book2 = new Book();
		book2.setTitle("Test Book 2");
		book2.setAuthor("Test Author 2");
		book2.setPublicationYear(2024);

		bookRepository.save(book1);
		bookRepository.save(book2);

		mockMvc.perform(get("/api/books")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].title").value("Test Book 1"))
				.andExpect(jsonPath("$[1].title").value("Test Book 2"));
	}
}