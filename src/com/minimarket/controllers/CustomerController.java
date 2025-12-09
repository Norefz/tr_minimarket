package com.minimarket.controllers;

import com.minimarket.services.CustomerService;
import com.minimarket.models.Customer;
import java.util.List;

public class CustomerController {
    private CustomerService customerService;

    public CustomerController() {
        this.customerService = new CustomerService();
    }

    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    public Customer getCustomerById(int id) {
        return customerService.getCustomerById(id);
    }

    public Customer getCustomerByPhone(String phone) {
        return customerService.getCustomerByPhone(phone);
    }

    public boolean addCustomer(Customer customer) {
        // Validate
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama customer harus diisi");
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor telepon harus diisi");
        }

        return customerService.addCustomer(customer);
    }

    public boolean updateCustomer(Customer customer) {
        return customerService.updateCustomer(customer);
    }

    public boolean updateCustomerPoints(int customerId, int points) {
        return customerService.updateCustomerPoints(customerId, points);
    }
}