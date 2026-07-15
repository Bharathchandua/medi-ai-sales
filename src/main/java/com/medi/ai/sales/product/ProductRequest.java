package com.medi.ai.sales.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String company,
        @NotBlank String batchNumber,
        @NotBlank String hsnCode,
        LocalDate manufactureDate,
        LocalDate expiryDate,
        @NotNull @Min(0) Integer quantity,
        @NotNull @DecimalMin("0.0") BigDecimal rate,
        @NotNull @DecimalMin("0.0") BigDecimal gstPercentage,
        @NotNull @DecimalMin("0.0") BigDecimal discountPercentage
) {
}

