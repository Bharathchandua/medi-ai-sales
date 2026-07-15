package com.medi.ai.sales.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record InvoiceRequest(
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotEmpty(message = "Invoice must contain at least one item") @Valid List<InvoiceItemRequest> items
) {
    public record InvoiceItemRequest(
            @NotNull(message = "Product ID is required") Long productId,
            @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity
    ) {
    }
}
