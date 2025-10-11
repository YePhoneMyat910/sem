package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App {
    private Connection con = null;

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 20; // try more times to ensure DB is ready
        for (int i = 0; i < retries; i++) {
            try {
                System.out.println("Connecting to database... attempt " + (i + 1));
                Thread.sleep(5000); // wait 5 seconds between retries

                con = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:33060/employees?allowPublicKeyRetrieval=true&useSSL=false",
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

        if (con == null) {
            System.out.println("No database connection. Exiting.");
            System.exit(-1);
        }
    }

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

    public static class Employee {
        public int emp_no;
        public String first_name;
        public String last_name;
        public String title;
        public int salary;
        public String dept_name;
        public String manager;
    }

    public Employee getEmployee(int empId) {
        if (con == null) {
            System.out.println("No database connection.");
            return null;
        }

        String query = "SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Employee emp = new Employee();
                emp.emp_no = rs.getInt("emp_no");
                emp.first_name = rs.getString("first_name");
                emp.last_name = rs.getString("last_name");
                return emp;
            } else {
                System.out.println("Employee not found.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee: " + e.getMessage());
            return null;
        }
    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary: " + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n"
            );
        }
    }

    /**
     * Gets all the current employees and their salaries.
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries() {
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";

            ResultSet rset = stmt.executeQuery(strSelect);

            ArrayList<Employee> employees = new ArrayList<>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints a list of employees and their salaries in columns.
     */
    public void printSalaries(ArrayList<Employee> employees) {
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        for (Employee emp : employees) {
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    public static void main(String[] args) {
        App app = new App();

        // Connect to DB
        app.connect();

        // Get all current employee salaries
        ArrayList<Employee> employees = app.getAllSalaries();

        if (employees != null) {
            System.out.println("Number of current salaries retrieved: " + employees.size());
            // Print all salaries
            app.printSalaries(employees);
        } else {
            System.out.println("No salary data founds.");
        }

        // Disconnect from DB
        app.disconnect();
    }
}
