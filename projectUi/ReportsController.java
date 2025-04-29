package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.example.Db_utils;

@SuppressWarnings("unused")

public class ReportsController {

    @FXML
    private Button backButton; // linked to fx:id in FXML

    @FXML
    private void handleViewPayHistory() {
        System.out.println("handleViewPayHistory clicked"); // debug
        StringBuilder report = new StringBuilder();
        try (Connection conn = Db_utils.getConnection()) {

            /*
            MAKE SURE COLUMN NAME ARE CORRECT FROM YOUR TABLES.
            INSTEAD OF emp_id, name and salaru
            I have empid, FName, Lname, and Salary in my employee 
             
             
            */

            String sql = "SELECT empid, Fname, Lname, Salary FROM employees ORDER BY empid";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                report.append("ID: ").append(rs.getInt("empid"))
                      .append(", Name: ").append(rs.getString("Fname")).append(" ").append(rs.getString("Lname"))
                      .append(", Salary: $").append(rs.getDouble("Salary"))
                      .append("\n");
            }
            showReport("Pay Statement History", report.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewPayByJobTitle() {
        System.out.println("handleViewPayByJobTitle clicked"); // debug
        StringBuilder report = new StringBuilder();
        try (Connection conn = Db_utils.getConnection()) {

            // JOIN TABLES TO GET PROPER COLUMNS

            String sql = "SELECT jt.job_title, SUM(e.salary) AS total_salary FROM employees e JOIN employee_job_titles ejt ON e.empid = ejt.empid JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id GROUP BY jt.job_title";
            
            //String sql = "SELECT jt.job_title, SUM(e.salary) AS total_salary FROM employees e JOIN employee_job_titles ejt ON e.empid = ejt.empid JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id GROUP BY jt.job_title";
   
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                report.append("Job Title: ").append(rs.getString("job_title"))
                      .append(", Total Pay: $").append(rs.getDouble("total_salary"))
                      .append("\n");
            }
            showReport("Total Pay by Job Title", report.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewPayByDivision() {
        System.out.println("handleViewPayByDivision clicked"); // debug
        StringBuilder report = new StringBuilder();

        try (Connection conn = Db_utils.getConnection()) {
            String sql = "SELECT d.Name AS division, COALESCE(SUM(e.salary), 0) AS total_salary " +
                         "FROM division d " +
                        "LEFT JOIN employee_division ed ON d.ID = ed.div_ID " +
                        "LEFT JOIN employees e ON ed.empid = e.empid " +
                        "GROUP BY d.Name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String divisionName = rs.getString("division");
                double totalSalary = rs.getDouble("total_salary");

                report.append("Division: ").append(divisionName)
                    .append(", Total Pay: ").append(String.format("$%,.2f", totalSalary))
                    .append("\n");
            }

            if (!hasData) {
                report.append("No division data available.");
            }

            showReport("Total Pay by Division", report.toString());

        } catch (Exception e) {
            e.printStackTrace(); // For production, consider using a Logger
        }
    }

    void showReport(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(500, 400);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("Admin.fxml")));
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
