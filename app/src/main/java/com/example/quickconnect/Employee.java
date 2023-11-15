package com.example.quickconnect;

public class Employee extends User {

    private String employeeRole;
    private String department;
    private Boolean isAvailable;



    public Employee(String userId, String email, String firstName, String lastName, String phonenumber, String address) {
        super(userId, email, firstName, lastName, phonenumber, address);
    }
    public Employee(User user, String employeeRole, String department, Boolean isAvailable) {
        super(user.getUserId(), user.getEmail(), user.getFirstName(), user.getLastName(),user.getPhonenumber(), user.getAddress());
        this.employeeRole= employeeRole;
        this.department = department;
        this.isAvailable = isAvailable;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
}
