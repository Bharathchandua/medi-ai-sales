package com.medi.ai.sales.invoice;

import com.medi.ai.sales.customer.Customer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private LocalDate invoiceDate;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    private BigDecimal subTotal;
    private BigDecimal totalGst;
    private BigDecimal totalDiscount;
    private BigDecimal grandTotal;

    protected Invoice() {
    }

    public Invoice(String invoiceNumber, LocalDate invoiceDate, Customer customer,
                   BigDecimal subTotal, BigDecimal totalGst, BigDecimal totalDiscount, BigDecimal grandTotal) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.customer = customer;
        this.subTotal = subTotal;
        this.totalGst = totalGst;
        this.totalDiscount = totalDiscount;
        this.grandTotal = grandTotal;
    }

    public Long getId() {
        return id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getTotalGst() {
        return totalGst;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
        for (InvoiceItem item : items) {
            item.setInvoice(this);
        }
    }
}
