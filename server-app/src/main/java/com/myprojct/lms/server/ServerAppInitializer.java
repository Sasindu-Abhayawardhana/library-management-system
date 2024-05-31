package com.myprojct.lms.server;

import com.myprojct.lms.server.db.SingletonConnection;
import com.myproject.lms.shared.to.Book;
import com.myproject.lms.shared.to.DbOperations;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerAppInitializer {

    // keep the client in a array list
    // Copy on Array List is thread safe ( Since ArrayList is not thread safe)
    public static final List<Socket> CLIENT_LIST = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {

//        create a server socket to accept the client

        try (ServerSocket serverSocket = new ServerSocket(5050)) {

            System.out.println("Server started on port 5050");

            // keep listen to the client
            while (true) {
                Socket localSocket = serverSocket.accept();
                CLIENT_LIST.add(localSocket);

                new Thread(() -> {
                    try (InputStream is = localSocket.getInputStream();
                         ObjectInputStream ois = new ObjectInputStream(is);
                         Statement stm = SingletonConnection.getInstance().getConnection().createStatement()) {


                        while (true) {
                            Book book = (Book) ois.readObject();

                            switch (book.getDbOperations()) {
                                case ADD -> {
                                    // use execute update here, since Insert Into return the effected row count,
                                    // executequery return the result test

                                    try {

                                        stm.executeUpdate(
                                                """
                                                        INSERT INTO book (isbn, title, author, publisher, category, price) 
                                                            VALUES (book.isbn, book.title, book.author, book.publisher, book.category,book.price)
                                                        """, Statement.RETURN_GENERATED_KEYS
                                        );

                                        ResultSet generatedKeys = stm.getGeneratedKeys();
                                        generatedKeys.next();
                                        int id = generatedKeys.getInt("id");
                                        System.out.println("Added Book with id " + id + " to the database");


                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                case READ -> {
                                    //ToDo
                                }
                                case UPDATE -> {
                                    try {
                                        int updatedRows = stm.executeUpdate(
                                                """
                                                        UPDATE book SET isbn=book.isbn, title=book.title, author=book.author, publisher=book.publisher, category=book.category, price=book.price
                                                        WHERE id=book.id OR isbn=book.isbn
                                                        """
                                        );

                                        if (updatedRows > 0) {
                                            System.out.println("Book Record is Inserted Successfully");
                                        }
                                        else {
                                            System.out.println("Book Record is not Inserted Successfully");
                                        }

                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                case DELETE -> {
                                    try {
                                        int deletedRows = stm.executeUpdate(
                                                """
                                                        DELETE FROM book
                                                        WHERE id=book.id OR isbn=book.isbn
                                                        """
                                        );

                                        if (deletedRows > 0) {
                                            System.out.println("Book Record is deleted");
                                        }
                                        else {
                                            System.out.println("Book Record is not deleted");
                                        }

                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                default ->
                                        throw new IllegalStateException("Unexpected value: " + book.getDbOperations());
                            }

                        }


                    } catch (IOException | ClassNotFoundException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
