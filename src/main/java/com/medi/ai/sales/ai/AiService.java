package com.medi.ai.sales.ai;

import com.medi.ai.sales.customer.Customer;
import com.medi.ai.sales.customer.CustomerRepository;
import com.medi.ai.sales.product.Product;
import com.medi.ai.sales.product.ProductRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService {

    private final ChatModel chatModel;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public AiService(ChatModel chatModel,
                      ProductRepository productRepository,
                      CustomerRepository customerRepository) {
        this.chatModel = chatModel;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public String chatWithRepository(String userMessage) {
        String keyword = userMessage.toLowerCase();
        StringBuilder context = new StringBuilder();

        // 1. Retrieve Data: Check what user is asking about
        if (keyword.contains("product") || keyword.contains("stock") || keyword.contains("medicine") || keyword.contains("item")) {
            List<Product> products = productRepository.findAll();
            context.append("Current Inventory Products List:\n");
            for (Product p : products) {
                context.append(String.format("- ID: %d, Name: %s, Company: %s, Quantity in stock: %d, Unit Rate: %s, GST: %s%%, Discount: %s%%\n",
                        p.getId(), p.getName(), p.getCompany(), p.getQuantity(), p.getRate().toString(), p.getGstPercentage().toString(), p.getDiscountPercentage().toString()));
            }
        } else if (keyword.contains("customer") || keyword.contains("client") || keyword.contains("people")) {
            List<Customer> customers = customerRepository.findAll();
            context.append("Registered Customers List:\n");
            for (Customer c : customers) {
                context.append(String.format("- ID: %d, Name: %s, Phone: %s, Email: %s, Address: %s\n",
                        c.getId(), c.getName(), c.getPhone(), c.getEmail() != null ? c.getEmail() : "N/A", c.getAddress() != null ? c.getAddress() : "N/A"));
            }
        } else {
            context.append("No specific database records retrieved. Answer using general medical or system guidance.\n");
        }

        // 2. Build the System Prompt (Instruction & Context)
        String systemPrompt = """
                You are a helpful, professional AI Stock and Sales Assistant for Medi-AI-Sales.
                You are answering questions for the store owner.
                
                Below is the private database context retrieved by the system:
                ---------------------
                %s
                ---------------------
                
                Instructions:
                1. Answer the user's question accurately using ONLY the context provided above if it relates to database queries.
                2. If the context does not contain the answer, politely state that you don't have that information.
                3. Keep the tone helpful, professional, and concise.
                
                User Question: %s
                """.formatted(context.toString(), userMessage);

        // 3. Call the AI model and return the reply
        try {
            return chatModel.call(systemPrompt);
        } catch (Exception e) {
            return "Error calling local AI model. Please verify Ollama is running and the tinyllama model is downloaded. Details: " + e.getMessage();
        }
    }
}
