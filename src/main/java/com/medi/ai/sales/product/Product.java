package com.medi.ai.sales.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String company;
    private String batchNumber;
    private String hsnCode;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal gstPercentage;
    private BigDecimal discountPercentage;

    protected Product() {
    }

    public Product(String name, String company, String batchNumber, String hsnCode,
                   LocalDate manufactureDate, LocalDate expiryDate, Integer quantity,
                   BigDecimal rate, BigDecimal gstPercentage, BigDecimal discountPercentage) {
        this.name = name;
        this.company = company;
        this.batchNumber = batchNumber;
        this.hsnCode = hsnCode;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.rate = rate;
        this.gstPercentage = gstPercentage;
        this.discountPercentage = discountPercentage;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void updateFrom(ProductRequest request) {
        this.name = request.name();
        this.company = request.company();
        this.batchNumber = request.batchNumber();
        this.hsnCode = request.hsnCode();
        this.manufactureDate = request.manufactureDate();
        this.expiryDate = request.expiryDate();
        this.quantity = request.quantity();
        this.rate = request.rate();
        this.gstPercentage = request.gstPercentage();
        this.discountPercentage = request.discountPercentage();
    }

    public void deductQuantity(Integer qty) {
        if (this.quantity < qty) {
            throw new IllegalArgumentException("Insufficient stock for product: " + this.name + ". Available: " + this.quantity);
        }
        this.quantity -= qty;
    }
}

