package com.medi.ai.sales.product;

import com.medi.ai.sales.common.ResourceNotFoundException;
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
import java.util.Optional;

// This annotation tells JUnit to use Mockito's testing engine
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    // We create a FAKE repository (Mockito will intercept database calls)
    @Mock
    private ProductRepository productRepository;

    // We create the REAL service we want to test, and inject our fake repository into it
    @InjectMocks
    private ProductService productService;

    // Test case 1: Successful product retrieval
    @Test
    public void testGetProductById_Success() {
        // Arrange (Setting up all 10 required constructor arguments)
        Product fakeProduct = new Product(
                "Paracetamol",
                "Cipla",
                "BATCH123",
                "HSN123",
                LocalDate.now(),
                LocalDate.now().plusYears(2),
                100,
                new BigDecimal("12.50"),
                new BigDecimal("12.00"),
                new BigDecimal("5.00")
        );

        // We use Spring's Reflection helper to set the private ID field to 1L
        ReflectionTestUtils.setField(fakeProduct, "id", 1L);

        // We say: When someone calls productRepository.findById(1L), return our fakeProduct
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(fakeProduct));

        // Act (Call the real method we want to test)
        ProductResponse response = productService.findById(1L);

        // Assert (Verify the output matches our fakeProduct data)
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Paracetamol", response.name());
        Assertions.assertEquals("Cipla", response.company());
        Assertions.assertEquals(100, response.quantity());
    }

    // Test case 2: Testing what happens when a product is not found
    @Test
    public void testGetProductById_NotFound_ThrowsException() {
        // Arrange: We configure our fake repository to return an empty optional
        Mockito.when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: We execute the call and assert that it correctly throws a ResourceNotFoundException
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(99L);
        });
    }
    @Test
    public void testDeductQuantity_Success() {
        // Arrange
        Product fakeProduct = new Product(
                "Paracetamol",
                "Cipla",
                "BATCH123",
                "HSN123",
                LocalDate.now(),
                LocalDate.now().plusYears(2),
                100,
                new BigDecimal("12.50"),
                new BigDecimal("12.00"),
                new BigDecimal("5.00")
        );

        // Act
        fakeProduct.deductQuantity(10);

        // Assert
        Assertions.assertEquals(90, fakeProduct.getQuantity());
    }
}