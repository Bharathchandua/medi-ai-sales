package com.medi.ai.sales.product;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductResponse(
        Long id,
        String name,
        String company,
        String batchNumber,
        String hsnCode,
        LocalDate manufactureDate,
        LocalDate expiryDate,
        Integer quantity,
        BigDecimal rate,
        BigDecimal gstPercentage,
        BigDecimal discountPercentage
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCompany(),
                product.getBatchNumber(),
                product.getHsnCode(),
                product.getManufactureDate(),
                product.getExpiryDate(),
                product.getQuantity(),
                product.getRate(),
                product.getGstPercentage(),
                product.getDiscountPercentage()
        );
    }
}

