package com.napier.sem;

import java.sql.*;

public class App {
    // Connection object for the whole class
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; i++) {
            try {
                System.out.println("Connecting to database...");
                Thread.sleep(30000); // Wait for DB to start

                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root",
                        "example"
                );
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed attempt " + i + ": " + sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Disconnected from database");
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Get employee info by ID
     */
    public void getEmployeeById(int empId) {
        if (con == null) {
            System.out.println("No database connection.");
            return;
        }

        String query = "SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Employee ID: " + rs.getInt("emp_no"));
                System.out.println("First Name: " + rs.getString("first_name"));
                System.out.println("Last Name: " + rs.getString("last_name"));
            } else {
                System.out.println("Employee not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee: " + e.getMessage());
        }
    }

    /**
     * Main method to run the application
     */
    public static void main(String[] args) {
        App app = new App();

        // Connect to DB
        app.connect();

        // Example: get employee with ID 10001
        app.getEmployeeById(10001);

        // Disconnect from DB
        app.disconnect();
    }
}
