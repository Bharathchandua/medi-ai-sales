package com.medi.ai.sales.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        LocalDate invoiceDate,
        Long customerId,
        String customerName,
        List<InvoiceItemResponse> items,
        BigDecimal subTotal,
        BigDecimal totalGst,
        BigDecimal totalDiscount,
        BigDecimal grandTotal
) {
    public record InvoiceItemResponse(
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal rate,
            BigDecimal gstPercentage,
            BigDecimal discountPercentage,
            BigDecimal totalAmount
    ) {
        public static InvoiceItemResponse from(InvoiceItem item) {
            return new InvoiceItemResponse(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getRate(),
                    item.getGstPercentage(),
                    item.getDiscountPercentage(),
                    item.getTotalAmount()
            );
        }
    }

    public static InvoiceResponse from(Invoice invoice) {
        List<InvoiceItemResponse> itemResponses = invoice.getItems().stream()
                .map(InvoiceItemResponse::from)
                .toList();

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getInvoiceDate(),
                invoice.getCustomer().getId(),
                invoice.getCustomer().getName(),
                itemResponses,
                invoice.getSubTotal(),
                invoice.getTotalGst(),
                invoice.getTotalDiscount(),
                invoice.getGrandTotal()
        );
    }
}
