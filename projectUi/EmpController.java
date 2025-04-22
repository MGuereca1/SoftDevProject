package com.example;


import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class EmpController implements Initializable {
    // fx:id's

    @FXML
    private Button button_logout; 

    @FXML
    private Label lbl_welcome;
    // ADD OTHER FUNCTIONS HERE FOR ADMIN

    //added more buttons :
    @FXML private Button btn_showData;
    @FXML private Label lbl_name, lbl_email, lbl_payroll, lbl_statement;

    private String loggedInUsername;  // Store username to fetch data

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // logout button goes back to login menu
        button_logout.setOnAction(event -> {
            Db_utils.changeScene(event,"primary.fxml", "log in", null,"employee");
        });
    }

    // return  username on welcome
    public void setUserInfo(String username){
        this.loggedInUsername = username; // Store the username
        lbl_welcome.setText("Welcome " + username + "!");

    }
    //updated
    @FXML
    private void showEmployeeData(ActionEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Db_utils.getConnection(); // assumes you have a getConnection() utility method

        // Step 1: Get employee ID from username
            String query = "SELECT empid FROM Users WHERE Username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, loggedInUsername);
            rs = stmt.executeQuery();

            int empid = -1;
            if (rs.next()) {
                empid = rs.getInt("empid");
            }
            rs.close();
            stmt.close();

            // Step 2: Get employee info
            query = "SELECT Fname, Lname, email FROM employees WHERE empid = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, empid);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("Fname") + " " + rs.getString("Lname");
                String email = rs.getString("email");

                lbl_name.setText("Name: " + fullName);
                lbl_email.setText("Email: " + email);
            }
            rs.close();
            stmt.close();

            // Step 3: Get latest payroll
            query = "SELECT earnings, pay_date FROM payroll WHERE empid = ? ORDER BY pay_date DESC LIMIT 3";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, empid);
            rs = stmt.executeQuery();

            double totalEarnings = 0;
            StringBuilder dates = new StringBuilder("Statement History: ");

            while (rs.next()) {
                totalEarnings += rs.getDouble("earnings");
                dates.append(rs.getDate("pay_date").toString()).append(", ");
            }

            lbl_payroll.setText(String.format("Payroll: $%.2f", totalEarnings));
            lbl_statement.setText(dates.toString().replaceAll(", $", ""));

        } catch (SQLException e) {
            e.printStackTrace();
            lbl_name.setText("Name: Error loading data");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
