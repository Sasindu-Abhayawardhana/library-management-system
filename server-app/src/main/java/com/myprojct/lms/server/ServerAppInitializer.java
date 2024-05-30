package com.myprojct.lms.server;

import com.myprojct.lms.server.db.SingletonConnection;
import com.myproject.lms.shared.to.Book;
import com.myproject.lms.shared.to.DbOperations;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
//                                    stm.executeUpdate();

                                }
                                case READ -> {
                                }
                                case UPDATE -> {
                                }
                                case DELETE -> {
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
