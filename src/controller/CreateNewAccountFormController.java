package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label lblPasswordNotMached1;
    public Label lblPasswordNotMached2;
    public TextField txtUsername;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblUserID;
    public AnchorPane root;

    public void initialize(){
        setLabelVisible(false);
        setDisableFields(true);
    }

    public void txtNewPasswordOnAction(ActionEvent actionEvent) {
    }

    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) {

        register();
    }

    public void register(){
        String newPasswordText = txtNewPassword.getText();
        String confirmPasswordText = txtConfirmPassword.getText();

        if(newPasswordText.equals(confirmPasswordText)){
            setBorderColor("Transparent");
            setLabelVisible(false);
            String userIDText = lblUserID.getText();
            String usernameText = txtUsername.getText();
            String emailText = txtEmail.getText();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                    PreparedStatement   preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?)");
                    preparedStatement.setObject(1,userIDText);
                    preparedStatement.setObject(2,usernameText);
                    preparedStatement.setObject(3,emailText);
                    preparedStatement.setObject(4,newPasswordText);
                    int i = preparedStatement.executeUpdate();
                    if(i!=0){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Success!");
                        alert.showAndWait();
                        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));
                        Scene scene = new Scene(parent);
                        Stage primaryStage = (Stage) root.getScene().getWindow();
                        primaryStage.setScene(scene);
                        primaryStage.setTitle("Login Form");
                        primaryStage.centerOnScreen();
                    }
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }

        }else{
            setBorderColor("Red");
            txtNewPassword.requestFocus();
            setLabelVisible(true);
        }

    }

    public void setBorderColor(String color){
        txtConfirmPassword.setStyle("-fx-border-color: " + color);
        txtNewPassword.setStyle("-fx-border-color: " + color);
    }

    public void setLabelVisible(boolean isVisible){
        lblPasswordNotMached1.setVisible(isVisible);
        lblPasswordNotMached2.setVisible(isVisible);
    }

    public void AddNewUserOnAction(ActionEvent actionEvent) {
        setDisableFields(false);
        txtUsername.requestFocus();
        Connection connection = DBConnection.getInstance().getConnection();
        autoGenerateID();
    }

    public void setDisableFields(boolean isDisable){
        txtUsername.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtNewPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
        btnRegister.setDisable(isDisable);
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");
            boolean isExist = resultSet.next();
            if(isExist){
                String userID = resultSet.getString(1);
                userID = userID.substring(1,4);
                System.out.println(userID);
                int intID = Integer.parseInt(userID);
                intID++;
                if(intID<10){
                    lblUserID.setText("U00"+intID);
                }else if(intID<100){
                    lblUserID.setText("U0"+intID);
                }else if(intID<1000){
                    lblUserID.setText("U"+intID);
                }

            }else{
                lblUserID.setText("U001");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
