package com.medi.ai.sales.invoice;

import com.medi.ai.sales.customer.Customer;
import com.medi.ai.sales.customer.CustomerRepository;
import com.medi.ai.sales.product.Product;
import com.medi.ai.sales.product.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    // 1. We mock the three repositories that InvoiceService needs to talk to
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;

    // 2. We inject these mocks into our real InvoiceService
    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    public void testCreateInvoice_Success() {
        // --- ARRANGE ---
        // Create a fake customer
        Customer fakeCustomer = new Customer("John Doe", "1234567890", "john@email.com", "123 Main St");
        ReflectionTestUtils.setField(fakeCustomer, "id", 1L);

        // Create a fake product (Quantity: 100, Rate: 10.00, GST: 12%, Discount: 10%)
        Product fakeProduct = new Product(
                "Paracetamol", "Cipla", "B1", "H1",
                LocalDate.now(), LocalDate.now().plusYears(1),
                100, new BigDecimal("10.00"), new BigDecimal("12.00"), new BigDecimal("10.00")
        );
        ReflectionTestUtils.setField(fakeProduct, "id", 1L);

        // Tell our mock repositories what to return when called
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(fakeCustomer));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(fakeProduct));

        // When saving the invoice, just return the invoice we saved (we mock the save method)
        Mockito.when(invoiceRepository.save(Mockito.any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Create the request payload: Customer 1 buys 10 items of Product 1
        InvoiceRequest.InvoiceItemRequest itemRequest = new InvoiceRequest.InvoiceItemRequest(1L, 10);
        InvoiceRequest invoiceRequest = new InvoiceRequest(1L, List.of(itemRequest));

        // --- ACT ---
        // Run the actual billing calculator in InvoiceService!
        InvoiceResponse response = invoiceService.create(invoiceRequest);

        // --- ASSERT (Verify the billing math) ---
        Assertions.assertNotNull(response);

        // 1. Math Check: Rate is 10.00, Qty is 10. So Subtotal = 100.00
        Assertions.assertEquals(0, new BigDecimal("100.00").compareTo(response.subTotal()));

        // 2. Math Check: GST is 12% on 100.00 = 12.00
        Assertions.assertEquals(0, new BigDecimal("12.00").compareTo(response.totalGst()));

        // 3. Math Check: Discount is 10% on 100.00 = 10.00
        Assertions.assertEquals(0, new BigDecimal("10.00").compareTo(response.totalDiscount()));

        // 4. Math Check: Grand Total = Subtotal (100) + GST (12) - Discount (10) = 102.00
        Assertions.assertEquals(0, new BigDecimal("102.00").compareTo(response.grandTotal()));

        // 5. Stock Check: Product stock was 100, we bought 10, so it must now be 90!
        Assertions.assertEquals(90, fakeProduct.getQuantity());
    }
}