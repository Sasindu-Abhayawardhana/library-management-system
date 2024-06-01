package com.myproject.lms.shared.to;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {

    DbOperations dbOperations;
    int bookId;
    String isbn;
    String title;
    String author;
    String publisher;
    String category;
    double price;

}

