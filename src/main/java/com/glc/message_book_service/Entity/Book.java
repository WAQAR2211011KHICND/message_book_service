package com.glc.message_book_service.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString
public class Book {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String author;
    private int year;
    private int pages;

    public Book() {
    }

    public Book(String title, String author, int year, int pages) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.pages = pages;
    }

}
