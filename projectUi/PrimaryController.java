package com.example;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    // login menu fx:id's
    
    @FXML
    private Button button_login;

    @FXML
    private TextField tf_username;

    @FXML
    private PasswordField pf_password;

    @FXML
    private TextField tf_passwordVisible;

    @FXML
    private ToggleButton show_bttn;

    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        if (show_bttn.isSelected()){
            // show password (make tf_password visible, passwordfield hidden)
            tf_passwordVisible.setVisible(true);
            tf_passwordVisible.setManaged(true);
            
            pf_password.setVisible(false);
            pf_password.setManaged(false);

            show_bttn.setText("Hide");
        }
        else{
            // if not selected do not set password visible
            tf_passwordVisible.setVisible(false);
            tf_passwordVisible.setManaged(false);

            pf_password.setVisible(true);
            pf_password.setManaged(true);

            show_bttn.setText("Show");
        }

    }
    
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        tf_passwordVisible.setManaged(false);
        tf_passwordVisible.setVisible(false);
        
        // sync password
        tf_passwordVisible.textProperty().bindBidirectional(pf_password.textProperty());;
        
        // set login button action
        button_login.setOnAction(new EventHandler<ActionEvent>() {       
            @Override
            public void handle(final ActionEvent event){
                // will make sure input from user is checked
                Db_utils.UserLogin(event, tf_username.getText(),pf_password.getText());
            }
            
        });
    }
}

