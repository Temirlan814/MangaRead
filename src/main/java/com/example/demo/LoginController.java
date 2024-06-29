package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import java.io.IOException;


public class LoginController {
    @FXML
    public Label errorMessageLabel;
    @FXML
    public TextField newusernameField;
    @FXML
    public TextField newvisiblePasswordField;
    @FXML
    public PasswordField newpasswordField;
    @FXML
    public Label newerrorMessageLabel;
    public Line line1;
    public Line line2;
    @FXML
    private TextField usernameField;
    @FXML
    private VBox loginBox;
    @FXML
    private VBox registrationBox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;

    @FXML
    private void registration() throws IOException {
        String username = newusernameField.getText().strip();
        String password = newpasswordField.getText();
        if(password.length()< 7){
            newerrorMessageLabel.setText("Пароль должен содержать не менее 8 символов");
        }
        else if(username.isEmpty()){
            newerrorMessageLabel.setText("Пользователь не должен быть пустым");
        }
        else if(username.contains(" ") || password.contains(" ")){
            newerrorMessageLabel.setText("У Пользователя или у пароля не должно быть пробелов");
        }
        else {
            if (!DatabaseController.addCredentials(username, password)) {
                newerrorMessageLabel.setText("Пользователь с таким логином уже существует");
            }
            else {
                HelloApplication.changeScene();
            }
        }
    }
    @FXML
    public void login() throws IOException {
        String username = usernameField.getText().strip();
        String password = passwordField.isVisible()? passwordField.getText() : visiblePasswordField.getText();
        if (DatabaseController.checkCredentials(username, password)) {
            HelloApplication.changeScene();
            System.out.println("Логин и/или пароль верны");
        } else {
            errorMessageLabel.setText("Логин и/или пароль неверны");
        }
    }

    @FXML
    private void changelogin(){
        changelogin(loginBox, registrationBox,line1,line2);
    }
    @FXML
    private void changeRegister() {
        changelogin(registrationBox, loginBox,line2,line1);
    }
    private void changelogin(VBox LBox, VBox RBox,Line Line1,Line Line2){
        boolean showLogin = LBox.isVisible();
        if(showLogin){
            LBox.setVisible(!showLogin);
            RBox.setVisible(showLogin);
            Line1.setVisible(!showLogin);
            Line2.setVisible(showLogin);
        }
        else{
            RBox.setVisible(!showLogin);
            LBox.setVisible(showLogin);
            Line2.setVisible(!showLogin);
            Line1.setVisible(showLogin);
        }
    }
    @FXML
    private void togglePasswordVisibility() {
        togglePasswordFieldVisibility(passwordField, visiblePasswordField);
    }

    @FXML
    private void newtogglePasswordVisibility() {
        togglePasswordFieldVisibility(newpasswordField, newvisiblePasswordField);
    }

    private void togglePasswordFieldVisibility(PasswordField passwordField, TextField visiblePasswordField) {
        boolean showPassword = passwordField.isVisible();
        passwordField.setVisible(!showPassword);
        visiblePasswordField.setVisible(showPassword);

        if (showPassword) {
            visiblePasswordField.setText(passwordField.getText());
        } else {
            passwordField.setText(visiblePasswordField.getText());
        }
    }
}
