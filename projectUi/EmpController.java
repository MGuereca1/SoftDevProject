package com.example;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmpController implements Initializable {

    // Labels
    @FXML private Label lbl_welcome;
    @FXML private Label lbl_name;
    @FXML private Label lbl_email;
    @FXML private Label lbl_salary;
    @FXML private Label lbl_ssn;

    // Table and columns
    @FXML private TableView<PayrollRecord> tbl_statementHistory;
    @FXML private TableColumn<PayrollRecord, String> col_payDate;
    @FXML private TableColumn<PayrollRecord, Double> col_earnings;
    @FXML private TableColumn<PayrollRecord, Double> col_fedTax;
    @FXML private TableColumn<PayrollRecord, Double> col_fedMed;
    @FXML private TableColumn<PayrollRecord, Double> col_fedSS;
    @FXML private TableColumn<PayrollRecord, Double> col_stateTax;
    @FXML private TableColumn<PayrollRecord, Double> col_retire401k;
    @FXML private TableColumn<PayrollRecord, Double> col_healthcare;

    /**
     * Sets the user info label after login.
     * This method should be called by the login controller.
     */
    public void setUserInfo(String name) {
        if (lbl_welcome != null) {
            lbl_welcome.setText("Welcome, " + name + "!");
        }
    }

    /**
     * Loads payroll data for the currently logged-in employee.
     */
    @FXML
    private void showEmployeeData(ActionEvent event) {
        System.out.println("Button clicked - loading data");
        // Clear table before populating
        tbl_statementHistory.getItems().clear();

        ObservableList<PayrollRecord> records = FXCollections.observableArrayList();

        try (Connection conn = Db_utils.getConnection()) {
            System.out.println("Connected to DB: " + (conn != null));
            System.out.println("Session.empid = " + Session.empid);
            
            // Query to select payroll data
            String sql = "SELECT * FROM payroll WHERE empid = ? ORDER BY pay_date";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Session.empid); // Assuming Session.empid is set at login

            ResultSet rs = stmt.executeQuery();

            // Clear existing records in the table before adding new ones
            records.clear(); // Clear the ObservableList before adding new data

            // Loop through the results and create PayrollRecord objects
            while (rs.next()) {
                records.add(new PayrollRecord(
                    rs.getDate("pay_date").toString(),  // pay_date column
                    rs.getDouble("earnings"),
                    rs.getDouble("fed_tax"),
                    rs.getDouble("fed_med"),           // fed_med column
                    rs.getDouble("fed_SS"),
                    rs.getDouble("state_tax"),
                    rs.getDouble("retire_401k"),
                    rs.getDouble("health_care")        // health_care column
                ));
            }

            // Populate the table with data
            tbl_statementHistory.setItems(records);

        } catch (Exception e) {
            e.printStackTrace(); // Replace with logger or user-friendly alert in production
        }
    }

    /**
     * Binds table columns to PayrollRecord properties.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        col_payDate.setCellValueFactory(new PropertyValueFactory<>("payDate"));
        col_earnings.setCellValueFactory(new PropertyValueFactory<>("earnings"));
        col_fedTax.setCellValueFactory(new PropertyValueFactory<>("fedTax"));
        col_fedMed.setCellValueFactory(new PropertyValueFactory<>("fedMed"));
        col_fedSS.setCellValueFactory(new PropertyValueFactory<>("fedSS"));
        col_stateTax.setCellValueFactory(new PropertyValueFactory<>("stateTax"));
        col_retire401k.setCellValueFactory(new PropertyValueFactory<>("retire401k"));
        col_healthcare.setCellValueFactory(new PropertyValueFactory<>("healthcare"));
    }

    /**
     * Handles the logout action and redirects to the login page.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        Db_utils.changeScene(event, "primary.fxml", "Log in", null, "admin");
        System.out.println("Admin has logged out.");
    }
    
}
