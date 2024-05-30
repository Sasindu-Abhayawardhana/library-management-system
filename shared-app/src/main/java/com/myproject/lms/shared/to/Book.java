package com.myproject.lms.shared.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    DbOperations dbOperations;
    int bookId;
    String isbn;
    String title;
    String author;
    String publisher;
    String category;
    String editor;
    int quantity;
    double price;

}

