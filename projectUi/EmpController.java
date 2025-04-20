package com.example;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


import java.net.URL;
import java.util.ResourceBundle;

public class EmpController implements Initializable {
    // fx:id's

    @FXML
    private Button button_logout; 

    @FXML
    private Label lbl_welcome;
    // ADD OTHER FUNCTIONS HERE FOR ADMIN

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // logout button goes back to login menu
        button_logout.setOnAction(event -> {
            Db_utils.changeScene(event,"primary.fxml", "log in", null,"employee");
        });
    }
// return  username on welcome
    public void setUserInfo(String username){
        lbl_welcome.setText("Welcome " + username + "!");

    }
}