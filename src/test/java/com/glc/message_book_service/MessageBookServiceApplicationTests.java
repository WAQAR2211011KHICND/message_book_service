package com.glc.message_book_service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glc.message_book_service.Controller.BookController;
import com.glc.message_book_service.Entity.Book;
import com.glc.message_book_service.Repository.BookRepository;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;




@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
class MessageBookServiceApplicationTests {
	

	private MockMvc mvc;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookController bookController;

	private JacksonTester<Book> jsonBook;
	private JacksonTester<Collection<Book>> jsonBooks;

	@BeforeEach
	public void setUp() {
		JacksonTester.initFields(this, new ObjectMapper());
		mvc = MockMvcBuilders.standaloneSetup(bookController).build();
	}

	@Test
	void contextLoads() {
	}

	// AC1: When I enter the title, author, year of publication, and length of the
	// book into the UI and hit submit, my book will saved to the list.
	@Test
	public void canCreateANewBook() throws Exception {
		Book book = new Book("The Hobbit", "J.R.R. Tolkein", 1937, 320);
		mvc.perform(post("/book/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBook.write(book).getJson()))
				.andExpect(status().isOk())
				.andExpect(content().json(jsonBook.write(book).getJson()));

	}

	// AC2: When I click “View All Books” the application will display a list of all
	// the books in my list.
	@Test
	public void canGetAllBooks() throws Exception {
		Book book1 = new Book( "The Hobbit", "J.R.R. Tolkein", 1937, 320);
		Book book2 = new Book( "It", "Stephen King", 1986, 1138);
		List<Book> books = new ArrayList<Book>();
		books.add(book1);
		books.add(book2);
		when(bookRepository.findAll()).thenReturn(books);
		mvc.perform(get("/book/all")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(jsonBooks.write(books).getJson()));
	}


	// AC3
	@Test
	public void canGetBook() throws Exception {
		Book book1 = new Book( "The Hobbit", "J.r.r. Tolken", 1927, 328);
		// Book book2 = new Book(2, "It", "Stephen King", 1986, 1138);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
		mvc.perform(get("/book/?id=1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBook.write(book1).getJson()))
				.andExpect(status().isOk())
				.andExpect(content().json(jsonBook.write(book1).getJson()));
	}


	//AC4
	@Test
	public void canDeleteBook() throws Exception{	

		//deleting the book (where book id is 1)
		doNothing().when(bookRepository).deleteById(1L);
				
		mvc.perform(delete("/book/delete/?id=1")
		.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string("Delete Successfuly"));	
		
		//throwing the error (where book id is not present or null)
		doThrow(new RuntimeException()).when(bookRepository).deleteById(2L);
		
		mvc.perform(delete("/book/delete/?id=2")
		  .contentType(MediaType.APPLICATION_JSON))
		  .andExpect(status().isBadRequest())
		  .andExpect(content().string("Book not Present: Invalid Id"));
		
	}


	// AC5
	@Test 
	public void canUpdateBook() throws Exception
	{	
		Book book = new Book("The Hobbit", "J.R.R. Tolkein", 1937, 320);
		book.setId(1L);

		Book expectedBook = new Book("The Hobbit", "Muhammad Waqar", 1937, 320);
		expectedBook.setId(1L);;

		when(bookRepository.findById( 1L )).thenReturn( Optional.of(book) );
		when(bookRepository.findById( 2L )).thenReturn( Optional.empty());

		when(bookRepository.save(expectedBook)).thenReturn(expectedBook);
		

		mvc.perform(put("/book/update")
		 .contentType(MediaType.APPLICATION_JSON)
		 .content(jsonBook.write(expectedBook).getJson()))
		 .andExpect(status().isOk());
		 
		expectedBook.setId(2L);

		mvc.perform(put("/book/update")
		 .contentType(MediaType.APPLICATION_JSON)
		 .content(jsonBook.write(expectedBook).getJson()))
		 .andExpect(status().is(406));
		
	}

}

