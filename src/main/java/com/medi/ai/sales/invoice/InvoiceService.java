package com.medi.ai.sales.invoice;

import com.medi.ai.sales.common.ResourceNotFoundException;
import com.medi.ai.sales.customer.Customer;
import com.medi.ai.sales.customer.CustomerRepository;
import com.medi.ai.sales.product.Product;
import com.medi.ai.sales.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PdfGeneratorService pdfGeneratorService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          CustomerRepository customerRepository,
                          ProductRepository productRepository,
                          PdfGeneratorService pdfGeneratorService) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        List<InvoiceItem> items = new ArrayList<>();

        for (InvoiceRequest.InvoiceItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemReq.productId()));

            // Deduct quantity from stock
            product.deductQuantity(itemReq.quantity());

            BigDecimal rate = product.getRate();
            BigDecimal gstPct = product.getGstPercentage();
            BigDecimal discPct = product.getDiscountPercentage();

            // quantity * rate
            BigDecimal itemSubtotal = rate.multiply(BigDecimal.valueOf(itemReq.quantity()));

            // tax = subtotal * gst / 100
            BigDecimal itemGst = itemSubtotal.multiply(gstPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // discount = subtotal * discount / 100
            BigDecimal itemDiscount = itemSubtotal.multiply(discPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // total = subtotal + tax - discount
            BigDecimal itemTotal = itemSubtotal.add(itemGst).subtract(itemDiscount);

            subTotal = subTotal.add(itemSubtotal);
            totalGst = totalGst.add(itemGst);
            totalDiscount = totalDiscount.add(itemDiscount);
            grandTotal = grandTotal.add(itemTotal);

            InvoiceItem invoiceItem = new InvoiceItem(product, itemReq.quantity(), rate, gstPct, discPct, itemTotal);
            items.add(invoiceItem);
        }

        String invoiceNumber = "INV-" + System.currentTimeMillis();

        Invoice invoice = new Invoice(
                invoiceNumber,
                LocalDate.now(),
                customer,
                subTotal,
                totalGst,
                totalDiscount,
                grandTotal
        );

        invoice.setItems(items);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return InvoiceResponse.from(savedInvoice);
    }

    public List<InvoiceResponse> findAll() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceResponse::from)
                .toList();
    }

    public InvoiceResponse findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return InvoiceResponse.from(invoice);
    }

    public java.io.ByteArrayInputStream getInvoicePdf(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return pdfGeneratorService.generateInvoicePdf(invoice);
    }
}
