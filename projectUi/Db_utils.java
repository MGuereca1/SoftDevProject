package com.example;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Db_utils {
   
    //modified to chnage to appropriate menu

    // establish a connection to the database
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/employeedata";  // Your DB URL
        String dbUser = "root";  // Your DB username
        String dbPassword = "password";  // Your DB password (make sure it's correct)

        // Establish and return the connection
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }
public static void changeScene(ActionEvent event, String fxmlFile, String Title, String username, String role) {
    try {
        // loads fxml file
        URL fxmlUrl = Db_utils.class.getResource("/com/example/" + fxmlFile);
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        
        if (username != null && role!=null) {

            switch (role.toLowerCase()) {
                case "admin":
                    AdminController adminController = loader.getController();
                    adminController.setUserInfo(username);                        
                    break;
                
                case "employee":
                    EmpController empController = loader.getController();
                    empController.setUserInfo(username);
                    break;
                
                default:
                    break;
            }
        }

        // Ensure root is not null before proceeding
        if (root == null) {
            System.out.println("Error: Failed to load " + fxmlFile);
            return;
        }

        // CHANGE STAGE (window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(Title);
        //stage.setScene(new Scene(root, 600, 400));
        stage.setScene(new Scene(root));
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Exception: " + e.getMessage());

        //System.out.println("Scene change failed for file: " + fxmlFile);
    }
}

     // Handles userLogin and determine the scene the user will get based on their role
    public static void UserLogin(ActionEvent event, String username, String password){
            // make sql query to check credentials
        String sqlcommand = "SELECT empid, password, salt, role FROM users WHERE BINARY username = ?";
            // added Binary to make the username case sensitive
            // Can also modify the encoding preferences on the sql database to do this

        
        // connect to database
        try (Connection myConn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            PreparedStatement myStmt = myConn.prepareStatement(sqlcommand);

            myStmt.setString(1, username);
            //myStmt.setString(2, password);
            
            try(ResultSet myRS = myStmt.executeQuery()){
                if(myRS.next()){

                    //stored hash and salt
                    String storedHash = myRS.getString("password");
                    String storedSalt = myRS.getString("salt");

                    //role
                    String role = myRS.getString("role");

                    // rehash the passwird with the stored salt
                    if(PasswordUtils.isPasswordCorrect(password, storedSalt, storedHash)){


                        // Store user session info (to be used in emp controller)
                        int empid = myRS.getInt("empid");

                        Session.empid = empid;
                        Session.username = username;
                        Session.role = role;



                        System.out.println("Login Succesful!");

                        String fxmlFile=null;
                        String Title=null;

                        switch (role.toLowerCase()) {
                            case "admin":
                                //fxmlFile = "Admin.fxml";
                                fxmlFile = "Admin.fxml";
                                Title = "Admin Menu";
                                break;
                                
                            case "employee":
                                fxmlFile = "Emp_menu.fxml";
                                Title = "Employee Menu";
                                break;
                        
                            default:
                                break;
                        }
                        changeScene(event, fxmlFile, Title, username, role);
                    } 
                    else{
                        System.out.println("User not found");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("username or password are incorrect");
                        alert.show();
                        }
                    
                } else{
                    System.out.println("User not found");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("username or password are incorrect");
                    alert.show();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Database connection failed.");
            alert.show();
        }
    }
    
}    
