package com.glc.message_book_service.Controller;
import java.util.Optional;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glc.message_book_service.Entity.Book;
import com.glc.message_book_service.Repository.BookRepository;


@RestController
@RequestMapping("/book")
@RabbitListener(queues = "book")
public class BookController {

    @Autowired
    private Queue queue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BookRepository bookRepository;


    @PostMapping("/add")
    public ResponseEntity<Book> createBook(@RequestBody Book book){
        try {
            this.bookRepository.save(book);
            
            Iterable<Book> books = this.bookRepository.findAll();
            
            ObjectMapper mapp = new ObjectMapper();
            books.forEach( b ->{
                try {
                    this.rabbitTemplate.convertAndSend(queue.getName(), mapp.writeValueAsString( b ));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
            return ResponseEntity.ok().body(book);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return  ResponseEntity.badRequest().build();
        }
        
    }

    
    
     // (GET) https:localhost:8080/book/all
    @GetMapping("/all") //GET https://Lo..../books/all
    public Iterable<Book> getAllBooks(){
        return this.bookRepository.findAll();
    }

    @GetMapping("/") //  (GET) https:localhost:8080/book/?id=1
    public Optional<Book> getBook(@RequestParam Long id){
       return this.bookRepository.findById(id);
    }

    
    @DeleteMapping("/delete/") //  (GET) https:localhost:8080/book/?id=1
    public ResponseEntity<String> deleteBook(@RequestParam Long id){
        try {    
            this.bookRepository.deleteById(id);
            return ResponseEntity.ok().body("Delete Successfuly");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Book not Present: Invalid Id");    
        } 
    }

    
    @PutMapping("/update")
    public ResponseEntity<Book> updateBook(@RequestBody Book book){
        
        Optional<Book> UpdatedBook = this.bookRepository.findById(book.getId());

        if(UpdatedBook.isPresent()){
            //get book for updating and then send
            return ResponseEntity.ok().body(this.bookRepository.save(book));
        }else{
            return ResponseEntity.status(406).build(); 
        }
    }


    @RabbitListener(queues = "book")
    private void reciever(String in) throws Exception{
        ObjectMapper mapp = new ObjectMapper();
        Book book = mapp.readValue(in, new TypeReference<Book>(){});
        System.out.println(book.toString());
    }
}