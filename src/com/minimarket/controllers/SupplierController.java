package com.minimarket.controllers;

import com.minimarket.services.SupplierService;
import com.minimarket.models.Supplier;
import java.util.List;

public class SupplierController {
    private SupplierService supplierService;

    public SupplierController() {
        this.supplierService = new SupplierService();
    }

    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    public boolean addSupplier(Supplier supplier) {
        if (supplier.getCode() == null || supplier.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Kode Supplier harus diisi!");
        }
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama Supplier harus diisi!");
        }
        return supplierService.addSupplier(supplier);
    }

    public boolean updateSupplier(Supplier supplier) {
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama Supplier harus diisi!");
        }
        return supplierService.updateSupplier(supplier);
    }

    public boolean deleteSupplier(int id) {
        return supplierService.deleteSupplier(id);
    }
}