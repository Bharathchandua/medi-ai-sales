package com.medi.ai.sales.invoice;

import com.medi.ai.sales.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private Product product;

    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal gstPercentage;
    private BigDecimal discountPercentage;
    private BigDecimal totalAmount;

    protected InvoiceItem() {
    }

    public InvoiceItem(Product product, Integer quantity, BigDecimal rate,
                       BigDecimal gstPercentage, BigDecimal discountPercentage, BigDecimal totalAmount) {
        this.product = product;
        this.quantity = quantity;
        this.rate = rate;
        this.gstPercentage = gstPercentage;
        this.discountPercentage = discountPercentage;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Product getProduct() {
        return product;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
