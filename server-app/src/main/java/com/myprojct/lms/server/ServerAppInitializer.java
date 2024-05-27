package com.myprojct.lms.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
                
                new Thread(()->{
                    try {
                        InputStream is = localSocket.getInputStream();
                        ObjectInputStream ois = new ObjectInputStream(is);
                        
                        while (true) {
//                            Book o = ois.readObject();
                        }
                        
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
