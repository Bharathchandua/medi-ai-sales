# Product Module Explained

The Product module is the first completed business module in this project.

It manages medical product stock.

## Files In Product Module

```text
product/
  Product.java
  ProductRequest.java
  ProductResponse.java
  ProductRepository.java
  ProductService.java
  ProductController.java
```

## 1. Product.java

`Product.java` is the entity class.

Entity means:

> A Java class that maps to a database table.

Important annotations:

```java
@Entity
@Table(name = "products")
```

Meaning:

> Create/map this class to the `products` table.

Important field:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Meaning:

> `id` is the primary key, and MySQL will auto-generate it.

Why `BigDecimal` is used:

> `BigDecimal` is used for money values like rate, GST, and discount because it avoids floating-point precision problems.

## 2. ProductRequest.java

`ProductRequest` is the request DTO.

DTO means:

> Data Transfer Object.

This class receives data coming from Swagger/Postman/frontend.

Example:

```java
@NotBlank String name
@NotNull @Min(0) Integer quantity
```

Meaning:

- `name` cannot be blank
- `quantity` cannot be null
- `quantity` cannot be negative

## 3. ProductResponse.java

`ProductResponse` is the response DTO.

It controls what data we send back to the client.

Why not return entity directly?

> Entity represents database structure. Response DTO represents API output. Keeping them separate gives better control and security.

This method converts entity to response:

```java
public static ProductResponse from(Product product)
```

## 4. ProductRepository.java

Repository talks to the database.

```java
public interface ProductRepository extends JpaRepository<Product, Long>
```

Meaning:

> This repository handles database operations for `Product`, whose ID type is `Long`.

Because it extends `JpaRepository`, we automatically get:

- `save`
- `findAll`
- `findById`
- `delete`

Custom search method:

```java
findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase
```

Meaning:

> Search products by name or company, ignoring uppercase/lowercase differences.

## 5. ProductService.java

Service contains business logic.

Controller should not directly talk to repository.

Correct flow:

```text
Controller -> Service -> Repository
```

Methods in ProductService:

- `create` creates a product
- `findAll` returns all products
- `findById` returns one product
- `search` searches by product/company name
- `update` updates product data
- `delete` deletes a product

Why service is needed:

> Service keeps business logic separate from API handling and database code.

## 6. ProductController.java

Controller receives API requests.

Base path:

```java
@RequestMapping("/api/products")
```

This means all APIs start with:

```text
/api/products
```

APIs:

```text
POST /api/products
GET /api/products
GET /api/products/{id}
GET /api/products/search?keyword=value
PUT /api/products/{id}
DELETE /api/products/{id}
```

## Create Product Flow

When user creates product from Swagger:

```text
JSON request
    |
    v
ProductController.create()
    |
    v
ProductService.create()
    |
    v
ProductRepository.save()
    |
    v
MySQL products table
    |
    v
ProductResponse returned
```

## Interview Explanation

The Product module follows a clean layered architecture. `ProductController` exposes REST APIs, `ProductRequest` validates incoming data, `ProductService` handles business logic, `ProductRepository` performs database operations using Spring Data JPA, `Product` maps to the database table, and `ProductResponse` sends clean response data back to the client.

