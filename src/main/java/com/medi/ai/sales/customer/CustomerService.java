package com.medi.ai.sales.customer;

import com.medi.ai.sales.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse create(CustomerRequest request) {
        Customer customer = new Customer(
                request.name(),
                request.email(),
                request.phone(),
                request.address()
        );

        return CustomerResponse.from(customerRepository.save(customer));
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getCustomer(id));
    }

    public List<CustomerResponse> search(String keyword) {
        return customerRepository.findByNameContainingIgnoreCaseOrPhoneContaining(keyword, keyword)
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = getCustomer(id);
        customer.updateFrom(request);
        return CustomerResponse.from(customer);
    }

    public void delete(Long id) {
        Customer customer = getCustomer(id);
        customerRepository.delete(customer);
    }

    private Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
