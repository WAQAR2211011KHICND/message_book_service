package com.glc.message_book_service.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.glc.message_book_service.Entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    // void updateBook(String title, String author, int year, int pages,Long id);
}
