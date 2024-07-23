package com.myprojct.lms.server;

import com.myprojct.lms.server.db.SingletonConnection;
import com.myproject.lms.shared.request.BookRequest;
import com.myproject.lms.shared.request.LoginRequest;
import com.myproject.lms.shared.request.Request;
import com.myproject.lms.shared.to.Book;
import com.myproject.lms.shared.to.DbOperations;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
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

            // keeping listen to the client
            while (true) {
                Socket localSocket = serverSocket.accept();
                CLIENT_LIST.add(localSocket);

                new Thread(() -> {
                    try {
                        // get the input and output stream socket of the client connected socket
                        InputStream is = localSocket.getInputStream();
                        OutputStream os = localSocket.getOutputStream();
                        ObjectInputStream ois = new ObjectInputStream(is);
                        ObjectOutputStream oos = new ObjectOutputStream(os);

                        // use a singleton class to get the DB connection
                        Connection connection = SingletonConnection.getInstance().getConnection();

                        if (connection == null) {
                            System.err.println("Connection is Null");
                            return;
                        }

                        // reading the client's request
                        while (true) {

                            /*client can send request regarding following
                            1. Login Request
                            2. Book Request (Add,remove,search,update)
                            3. Payment Details //TODO
                            * */
                            Request request = (Request) ois.readObject();

                            // Login Request
                            if (request instanceof LoginRequest) {

                                LoginRequest loginRequest = (LoginRequest) request;
                                if (!loginRequest.getIsUserExit()) {

                                    String username = loginRequest.getUsername();

                                    // check the user validity
                                    // use the preparedStatement to avoid the sql injection
                                    PreparedStatement stm = connection.prepareStatement(
                                            """
                                                                                                    
                                                        SELECT * FROM admin_users WHERE username = ?
                                                    """
                                    );
                                    // set the username
                                    stm.setString(1, username);
                                    ResultSet resultSet = stm.executeQuery();
                                    if(resultSet.next()){
                                        System.out.println("User Exited Successfully" );
                                        loginRequest.setIsUserExit(true); // if the user exist set the update the login req boolean
                                        String hashPassword = resultSet.getString("password");
                                        System.out.println(hashPassword);
                                        loginRequest.setPassword(hashPassword);
                                        oos.writeObject(loginRequest); // send the response to the client
                                    }

                                }


                            } else if (request instanceof BookRequest) {

                                try (Statement stm = connection.createStatement()) {
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
                                                                """, Statement.

                                                                RETURN_GENERATED_KEYS
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
                                                                UPDATE book SET isbn=book.isbn, title=book.title, author=book.author,publisher=book.publisher, category
                                                                    =book.category WHERE id =book.id OR isbn=book.isbn
                                                                """);
                                                if (updatedRows > 0) {
                                                    System.out.println(
                                                            "Book Record is Inserted Successfully");
                                                } else {
                                                    System.out.println(
                                                            "Book Record is not Inserted Successfully");
                                                }

                                            } catch (
                                                    SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        case DELETE -> {
//                                            try {
//                                                int deletedRows = stm.executeUpdate(
//
//                                                        """
//                                                                DELETE FROM WHERE id=
//                                                                                            """
//                                                );
//
//                                                if (deletedRows > 0) {
//                                                    System.out.println(
//                                                            "Book Record is deleted");
//                                                } else {
//                                                    System.out.println("Book Record is not deleted");
//                                                }
//
//                                            } catch (SQLException e) {
//                                                throw new RuntimeException(e);
//                                            }
                                        }
                                        default ->
                                                throw new IllegalStateException("Unexpected value: " + book.getDbOperations());
                                    }
                                }
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
