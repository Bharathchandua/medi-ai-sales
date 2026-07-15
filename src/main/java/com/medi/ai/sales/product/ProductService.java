package com.medi.ai.sales.product;

import com.medi.ai.sales.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(
                request.name(),
                request.company(),
                request.batchNumber(),
                request.hsnCode(),
                request.manufactureDate(),
                request.expiryDate(),
                request.quantity(),
                request.rate(),
                request.gstPercentage(),
                request.discountPercentage()
        );

        return ProductResponse.from(productRepository.save(product));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    public List<ProductResponse> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        product.updateFrom(request);
        return ProductResponse.from(product);
    }

    public void delete(Long id) {
        Product product = getProduct(id);
        productRepository.delete(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}

