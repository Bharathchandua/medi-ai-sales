package com.medi.ai.sales.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank(message = "Name is required") String name,
        @Email(message = "Invalid email format") String email,
        @NotBlank(message = "Phone number is required") String phone,
        String address
) {
}
