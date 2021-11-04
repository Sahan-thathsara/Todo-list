package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {
    public AnchorPane root;
    public Label lblTitle;
    public Label lblUser;
    public Pane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM> lstToDo;
    public TextField txtSelected;
    public Button btnDelete;
    public Button btnUpdate;
    public String selectedID;

    public void initialize(){
        lblTitle.setText("Hi " + LoginPageFormController.passedUserName + ", Welcome to To Do List");
        lblUser.setText(LoginPageFormController.passedUserID);
        subRoot.setVisible(false);
        disableCommon(true);

        loadList();

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {

                disableCommon(false);
                subRoot.setVisible(false);
                ToDoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();
                if(selectedItem==null){
                    return;
                }
                txtSelected.setText(selectedItem.getDescription());
                System.out.println(selectedItem.getDescription());
                selectedID = selectedItem.getId();
            }
        });


    }

    public void loadList(){
        ObservableList<ToDoTM> toDoS = lstToDo.getItems();
        toDoS.clear();
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id=?");
            preparedStatement.setObject(1,LoginPageFormController.passedUserID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                ToDoTM toDoTM = new ToDoTM(id,description,user_id);

                toDoS.add(toDoTM);

            }

            lstToDo.refresh();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you Sure?",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if(buttonType.get().equals(ButtonType.YES)) {
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));
            Scene scene = new Scene(parent);
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Page");
            primaryStage.centerOnScreen();
        }
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        subRoot.setVisible(true);
        disableCommon(true);
        lstToDo.getSelectionModel().clearSelection();
        txtDescription.clear();
    }

    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");
            boolean isExist = resultSet.next();
            if(isExist){
                String userID = resultSet.getString(1);
                userID = userID.substring(1,4);
                System.out.println(userID);
                int intID = Integer.parseInt(userID);
                intID++;
                if(intID<10){
                    return "T00" + intID;
                }else if(intID<100){
                    return "T0" + intID;
                }else if(intID<1000){
                    return "T" + intID;
                }

            }else{
                return "T001";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void btnAddToDoListOnAction(ActionEvent actionEvent) {
        String description = txtDescription.getText();
        String user_id = lblUser.getText();
        String id = autoGenerateID();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values(?,?,?)");
            preparedStatement   .setObject(1,id);
            preparedStatement   .setObject(2,description);
            preparedStatement   .setObject(3,user_id);

            preparedStatement.executeUpdate();

            txtDescription.clear();;
            subRoot.setVisible(false);

            loadList();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
    public void disableCommon(boolean isDisable){
        btnUpdate.setDisable(isDisable);
        btnDelete.setDisable(isDisable);
        txtSelected.setDisable(isDisable);
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are You sure",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id=?");
                preparedStatement.setObject(1,selectedID);
                preparedStatement.executeUpdate();

                loadList();
                txtSelected.clear();
                disableCommon(true);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {

        Connection connection = DBConnection.getInstance().getConnection();
        String initialText = txtSelected.getText();
        ToDoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();


        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id =?");
            preparedStatement.setObject(1,initialText);
            preparedStatement.setObject(2,selectedID);
            preparedStatement.executeUpdate();

            loadList();

            txtSelected.clear();

            disableCommon(true);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
