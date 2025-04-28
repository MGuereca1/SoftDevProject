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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


public class AdminController implements Initializable {
    // fx:id's

    @FXML
    private Button button_logout; 

    @FXML
    private Label label_welcome;
    // ADD OTHER FUNCTIONS HERE FOR ADMIN

    //added the following
    @FXML private TableView<Employee> table_employees;
    @FXML private TableColumn<Employee, Integer> col_empid;
    @FXML private TableColumn<Employee, String> col_name, col_email, col_salary,col_ssn, col_hireDate;
    @FXML private TextField tf_name, tf_email, tf_salary,tf_empid, tf_ssn;
    @FXML private Button btn_add, btn_update, btn_delete;
    @FXML private TextField searchField;  
    @FXML private Button btn_search;

    @FXML private Button btn_showData;
    @FXML private Button btn_backToMenu;
    @FXML private DatePicker dp_hireDate;


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
        col_empid.setCellValueFactory(cellData -> cellData.getValue().empidProperty().asObject());
        col_name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        col_email.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        col_ssn.setCellValueFactory(cellData -> cellData.getValue().ssnProperty());
        col_hireDate.setCellValueFactory(cellData -> cellData.getValue().hireDateProperty());
        col_salary.setCellValueFactory(cellData -> cellData.getValue().salaryProperty());

        loadEmployeesFromDatabase();

        table_employees.setItems(employeeList);  // Bind data list

        table_employees.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tf_empid.setText(String.valueOf(newSelection.getEmpid()));
                tf_name.setText(newSelection.getName());
                tf_email.setText(newSelection.getEmail());
                tf_ssn.setText(newSelection.getSsn());
    
                try {
                    dp_hireDate.setValue(java.time.LocalDate.parse(newSelection.getHireDate()));
                } catch (Exception e) {
                    dp_hireDate.setValue(null);
                }
    
                tf_salary.setText(newSelection.getSalary().replace("$", "").replace(",", ""));
            }
        });
    }
    

    //method to load employee database
    private void loadEmployeesFromDatabase() {
        //debug: can delete later
        System.out.println("Connecting to DB...");


        employeeList.clear();

        //debug
        try (Connection conn = Db_utils.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM employees");
            ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("Employees in DB: " + total);  // This will print the number of employees in your DB
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lbl_searchResult.setText("Error loading employee count.");
        }
        //-------------------------------------------------------------------------

        try (Connection conn = Db_utils.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT empid, Fname, Lname, email, HireDate, Salary, SSN FROM employees");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int empid = rs.getInt("empid");
                String fullName = rs.getString("Fname") + " " + rs.getString("Lname");
                String email = rs.getString("email");
                String ssn = rs.getString("ssn");
                String hireDate = rs.getString("HireDate");
                double salary = rs.getDouble("Salary");

                //debug 
                System.out.println("Loaded employee: " + fullName);

                Employee emp = new Employee(empid, fullName, email, ssn, hireDate, String.format("$%.2f", salary));
                employeeList.add(emp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lbl_searchResult.setText("Error loading employees.");
        }

        //debuggin : can delete later
        if (employeeList.isEmpty()) {
            System.out.println("No employees loaded from DB — adding test data.");
        
            // Example test employee
            Employee testEmp = new Employee(
                999,                             // empid
                "Test User",                     // name
                "test@example.com",              // email
                "123-45-6789",                   // ssn
                "1990-01-01",                    // dob
                "$1,000.00"                      // payroll
            );
        
            employeeList.add(testEmp);
        }
        
        // Refresh table
        table_employees.setItems(employeeList);
    
    }


    @FXML
    private void handleAdd() {
        if (tf_name.getText().isEmpty() || tf_email.getText().isEmpty() || tf_salary.getText().isEmpty()) {
            showAlert(AlertType.WARNING, "Missing Data", "All fields are required.");
            return; // Optional: Show alert if fields are empty
        }
        
        try (Connection conn = Db_utils.getConnection()) {
            String[] names = tf_name.getText().split(" ", 2);
            String fname = names[0];
            String lname = names.length > 1 ? names[1] : "";

            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO employees (empid, Fname, Lname, email, HireDate, Salary, SSN) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, Integer.parseInt(tf_empid.getText()));
            stmt.setString(2, fname);
            stmt.setString(3, lname);
            stmt.setString(4, tf_email.getText());
            stmt.setString(5, dp_hireDate.getValue().toString());
            stmt.setDouble(6, Double.parseDouble(tf_salary.getText().replace("$", "").replace(",", "")));
            stmt.setString(7, tf_ssn.getText());

            stmt.executeUpdate();
            //successfully added employee 
            showAlert(AlertType.INFORMATION, "Success", "Employee added.");
            loadEmployeesFromDatabase();
            clearFields();

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Error", "Could not add employee."); //error
            e.printStackTrace();
        }
    }
    @FXML
    private void handleUpdate() {
        Employee selected = table_employees.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a row to update.");
            return;
        }
    
        try (Connection conn = Db_utils.getConnection()) {
            String[] names = tf_name.getText().split(" ", 2);
            String fname = names[0];
            String lname = names.length > 1 ? names[1] : "";
    
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE employees SET Fname=?, Lname=?, email=?, ssn=?, HireDate=?, Salary=? WHERE empid=?"
            );
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setString(3, tf_email.getText());
            stmt.setString(4, tf_ssn.getText());
            stmt.setString(5, dp_hireDate.getValue().toString());
            stmt.setDouble(6, Double.parseDouble(tf_salary.getText().replaceAll("[$,]", "")));
            stmt.setInt(7, selected.getEmpid());
    
            stmt.executeUpdate();
    
            showAlert(Alert.AlertType.INFORMATION, "Updated", "Employee record updated.");
            loadEmployeesFromDatabase();
            clearFields();
            table_employees.getSelectionModel().clearSelection();
    
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not update employee.");
            e.printStackTrace();
        }
    }
    
    
        

    @FXML
    private void handleDelete() {
        Employee selected = table_employees.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = Db_utils.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM employees WHERE empid=?");
            stmt.setInt(1, selected.getEmpid());
            stmt.executeUpdate();

            showAlert(AlertType.INFORMATION, "Deleted", "Employee removed.");
            loadEmployeesFromDatabase();
            clearFields();
            table_employees.getSelectionModel().clearSelection();
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Error", "Could not delete employee.");
            e.printStackTrace();
        }
    }
    // Handler for search button (btn_search)
        @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            table_employees.setItems(employeeList);
            lbl_searchResult.setText("Showing all employees.");
            return;
        }

        ObservableList<Employee> filteredList = FXCollections.observableArrayList();
        for (Employee emp : employeeList) {
            if (String.valueOf(emp.getEmpid()).contains(searchText) ||
                emp.getName().toLowerCase().contains(searchText) ||
                emp.getEmail().toLowerCase().contains(searchText) ||
                emp.getSsn().toLowerCase().contains(searchText)) {
                filteredList.add(emp);
            }
        }

        table_employees.setItems(filteredList);
        lbl_searchResult.setText(filteredList.isEmpty()
        // DEBUG: Print the size of the filtered list
            ? "No employees found."
            : "Showing results for: " + searchText);
    }

    private void clearFields() {
        tf_name.clear();
        tf_email.clear();
        tf_ssn.clear();
        tf_salary.clear();
        tf_empid.clear();
        dp_hireDate.setValue(null);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }



    // Handle showing employee data - might be for viewing all employees or detailed data
    @FXML
    private void showEmployeeData(ActionEvent event) {
        // Here you can call your method that shows employee data
        // E.g., displaying a table of all employees or specific data
        System.out.println("Show Employee Data clicked");
    }


    @FXML
    private void handleBackToMenu(ActionEvent event) {
        Db_utils.changeScene(event, "Admin.fxml", "Admin Menu", null, "admin");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Db_utils.changeScene(event, "primary.fxml", "Log in", null, "admin");
        System.out.println("Admin has logged out.");
    }
    //TODO: GENERATE TOTAL PAY JOB BY TITLE

    //TODO: GENERATE TOTAL PAY BY DIVISION REPORT

    //---------------------------------------------------------------------------------
    // return  username on welcome
    public void setUserInfo(String username){
        label_welcome.setText("Welcome " + username + "!");

    }
}
