package com.myproject.lms.admin.controller;

import com.myproject.lms.shared.request.LoginRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class LoginViewController {

    public Button btnUsernameLogin;
    public TextField txtUsername;
    public Button btnPasswordLogin;
    public TextField txtPassword;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket remoteSocket;

    private final int TIMEOUT_MILLISECONDS = 5000;

    public void initialize() throws IOException {
        remoteSocket = new Socket("localhost", 5050);
        System.out.println("Connected to remote socket" + remoteSocket.getInetAddress().getHostAddress());
        oos = new ObjectOutputStream(remoteSocket.getOutputStream());
        ois = new ObjectInputStream(remoteSocket.getInputStream());
    }

    public void btnUsernameLoginOnAction(ActionEvent actionEvent) throws IOException {
        //ToDO Validation

        String username = txtUsername.getText();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);

        boolean userNameStatus = false;

        try {
            oos.writeObject(loginRequest);
//            userNameStatus = ois.readBoolean();
            userNameStatus = ((LoginRequest)ois.readObject()).getIsUserExit();
            System.out.println("userNameStatus: " + userNameStatus);
//            remoteSocket.setSoTimeout(TIMEOUT_MILLISECONDS);

            if(userNameStatus){
                loginRequest.setIsUserExit(true);
                Stage stage = new Stage();
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/LoginViewUserPassword.fxml"))));
                stage.setResizable(false);
                ((Stage) btnUsernameLogin.getScene().getWindow()).close();
                stage.show();
                stage.centerOnScreen();
            } else{
                new Alert(Alert.AlertType.INFORMATION, "Try Again !!!").showAndWait();
            }

        } catch (SocketTimeoutException e) {
            new Alert(Alert.AlertType.ERROR, "Server Timeout. Please try again.").showAndWait();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

public void btnPasswordLoginOnAction(ActionEvent actionEvent) {
}
}
