package com.example;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


public class AdminController implements Initializable {
    //method to load employee database
    private void loadEmployeesFromDatabase() {
        employeeList.clear();

        try (Connection conn = Db_utils.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT Fname, Lname, email, empid FROM employees");
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String fullName = rs.getString("Fname") + " " + rs.getString("Lname");
                String email = rs.getString("email");

                // Fetch payroll info (latest)
                double payrollAmount = 0.0;
                try (PreparedStatement payrollStmt = conn.prepareStatement(
                        "SELECT earnings FROM payroll WHERE empid = ? ORDER BY pay_date DESC LIMIT 1")) {
                    payrollStmt.setInt(1, rs.getInt("empid"));
                    try (ResultSet payrollRs = payrollStmt.executeQuery()) {
                        if (payrollRs.next()) {
                            payrollAmount = payrollRs.getDouble("earnings");
                        }
                    }
                    
                }

                Employee emp = new Employee(fullName, email, String.format("$%.2f", payrollAmount));
                employeeList.add(emp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lbl_searchResult.setText("Error loading employees.");
        }

        table_employees.setItems(employeeList); // Set initial table view
    }

    // fx:id's

    @FXML
    private Button button_logout; 

    @FXML
    private Label label_welcome;
    // ADD OTHER FUNCTIONS HERE FOR ADMIN

    //added the following
    @FXML private TableView<Employee> table_employees;
    @FXML private TableColumn<Employee, String> col_name, col_email, col_payroll;
    @FXML private TextField tf_name, tf_email, tf_payroll;
    @FXML private Button btn_add, btn_update, btn_delete;
    @FXML private TextField searchField;  
    @FXML private Button btn_search;

    @FXML private Button btn_showData;
    @FXML private Button btn_backToMenu;


    @FXML private Label lbl_searchResult; // label to show the result of the search
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    //----------------------------------------------------------------------------------------
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // logout button goes back to login menu
        button_logout.setOnAction(event -> {
            Db_utils.changeScene(event,"primary.fxml", "Log in", null,"admin");
        });
    
        //added the following:
        col_name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        col_email.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        col_payroll.setCellValueFactory(cellData -> cellData.getValue().payrollProperty());
        loadEmployeesFromDatabase();

        table_employees.setItems(employeeList);  // Bind data list
    }

    @FXML
    private void handleAdd() {
        if (tf_name.getText().isEmpty() || tf_email.getText().isEmpty() || tf_payroll.getText().isEmpty()) {
            return; // Optional: Show alert if fields are empty
        }
        Employee emp = new Employee(tf_name.getText(), tf_email.getText(), tf_payroll.getText());
        employeeList.add(emp);
        table_employees.refresh();
        clearFields();
    }

    @FXML
    private void handleUpdate() {
        Employee selected = table_employees.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(tf_name.getText());
            selected.setEmail(tf_email.getText());
            selected.setPayroll(tf_payroll.getText());
            table_employees.refresh();
        }
        clearFields();
    }

    @FXML
    private void handleDelete() {
        Employee selected = table_employees.getSelectionModel().getSelectedItem();
        if (selected != null) {
            employeeList.remove(selected);
            table_employees.refresh();
        }
        clearFields();
    }
    
    // Handler for search button (btn_search)
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        // DEBUG: Print what user is searching
        System.out.println("Search text: " + searchText);
        System.out.println("Initial employee list size: " + employeeList.size());

        // If search field is empty, reset the table
        if (searchText.isEmpty()) {
            table_employees.setItems(employeeList);
            lbl_searchResult.setText("Showing all employees.");
            return;
        }

        ObservableList<Employee> filteredList = FXCollections.observableArrayList();

        for (Employee emp : employeeList) {
            System.out.println("Checking: " + emp.getName() + ", " + emp.getEmail());
            if (emp.getName().toLowerCase().contains(searchText) ||
                emp.getEmail().toLowerCase().contains(searchText)) {
                filteredList.add(emp);
            }
        }

        // DEBUG: Print the size of the filtered list
        System.out.println("Filtered list size: " + filteredList.size());

        // Update the table with filtered results
        table_employees.setItems(filteredList);

        if (filteredList.isEmpty()) {
            lbl_searchResult.setText("No employees found.");
        } else {
            lbl_searchResult.setText("Showing results for: " + searchText);
        }
    }



    // Handle showing employee data - might be for viewing all employees or detailed data
    @FXML
    private void showEmployeeData(ActionEvent event) {
        // Here you can call your method that shows employee data
        // E.g., displaying a table of all employees or specific data
        System.out.println("Show Employee Data clicked");
    }


    private void clearFields() {
        tf_name.clear();
        tf_email.clear();
        tf_payroll.clear();
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) {
        Db_utils.changeScene(event, "Admin.fxml", "Admin Menu", null, "admin");
    }

    //TODO: GENERATE TOTAL PAY JOB BY TITLE

    //TODO: GENERATE TOTAL PAY BY DIVISION REPORT

    //---------------------------------------------------------------------------------
    // return  username on welcome
    public void setUserInfo(String username){
        label_welcome.setText("Welcome " + username + "!");

    }
}
