package com.example;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    // fx:id's

    @FXML
    private Button button_logout; 

    @FXML
    private Label label_welcome;
    // ADD OTHER FUNCTIONS HERE FOR ADMIN

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // logout button goes back to login menu
        button_logout.setOnAction(event -> {
            Db_utils.changeScene(event,"primary.fxml", "log in", null,"admin");
        });
    }
// return  username on welcome
    public void setUserInfo(String username){
        label_welcome.setText("Welcome " + username + "!");

    }
}