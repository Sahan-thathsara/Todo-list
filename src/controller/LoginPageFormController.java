package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPageFormController {
    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;
    public static String passedUserID;
    public static String passedUserName;

    public void lblCreateNewAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Create account");
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        login();
    }

    public void txtPasswordOnAction(ActionEvent actionEvent) {
        login();
    }
    public void login() {
        if (txtUserName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Fields can not be empty!");
            alert.showAndWait();
            txtUserName.requestFocus();
        } else if (txtPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Fields can not be empty!");
            alert.showAndWait();
            txtUserName.requestFocus();
        }else{
            String userNameText = txtUserName.getText();
            String passwordText = txtPassword.getText();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from user where user_name = ? and password = ?");
                preparedStatement.setObject(1,userNameText);
                preparedStatement.setObject(2,passwordText);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){

                    passedUserID = resultSet.getString(1);
                    passedUserName = resultSet.getString(2);

                    System.out.println("if");
                    Parent parent = FXMLLoader.load(this.getClass().getResource("../view/ToDoForm.fxml"));
                    Scene scene = new Scene(parent);
                    Stage primaryStage = (Stage) root.getScene().getWindow();

                    primaryStage.setScene(scene);
                    primaryStage.centerOnScreen();
                    primaryStage.setTitle("To Do List");

                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Username or password does not match!");
                    alert.showAndWait();
                    txtUserName.clear();
                    txtPassword.clear();
                    txtUserName.requestFocus();
                }
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }


        }
    }
}
