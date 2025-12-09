package com.minimarket.controllers;

import com.minimarket.services.ProductService;
import com.minimarket.models.Product;
import java.util.List;

public class ProductController {
    private ProductService productService;

    public ProductController() {
        this.productService = new ProductService();
    }

    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    public Product getProductById(int id) {
        return productService.getProductById(id);
    }

    public Product getProductByCode(String code) {
        return productService.getProductByCode(code);
    }

    public boolean addProduct(Product product) {
        // Validate product
        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Kode produk harus diisi");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama produk harus diisi");
        }
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Harga harus lebih dari 0");
        }

        return productService.addProduct(product);
    }

    public boolean updateProduct(Product product) {
        if (product.getId() <= 0) {
            throw new IllegalArgumentException("ID produk tidak valid");
        }

        return productService.updateProduct(product);
    }

    public boolean deleteProduct(int id) {
        return productService.deleteProduct(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productService.searchProducts(keyword);
    }

    public boolean checkStock(int productId, int quantity) {
        Product product = productService.getProductById(productId);
        return product != null && product.getStock() >= quantity;
    }

    public List<Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }
}