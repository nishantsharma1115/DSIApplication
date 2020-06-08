package com.application.dsi.dataClass;

import java.util.ArrayList;

public class RequestCall {
    private int status;
    private String message;
    private Employee employee;
    private ArrayList<Customer> customers;
    private Customer customer;

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
