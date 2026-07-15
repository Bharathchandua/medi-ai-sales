# Swagger From Zero

Swagger is a browser page where we can see and test backend APIs.

In this project, Swagger opens at:

```text
http://localhost:8080/swagger-ui.html
```

## Why Swagger Is Used

When we build a backend, we create APIs.

Example APIs in this project:

```text
POST /api/products
GET /api/products
GET /api/products/{id}
PUT /api/products/{id}
DELETE /api/products/{id}
GET /api/products/search
```

Swagger shows these APIs in the browser.

It helps us know:

- what APIs are available
- which HTTP method is used
- what request body is needed
- what response comes back
- whether the API is working

## Swagger Is Not The Backend

Swagger is only a testing/documentation page.

The real backend is the Spring Boot application.

Flow when we create a product:

```text
Swagger UI
    |
    v
POST /api/products
    |
    v
ProductController
    |
    v
ProductService
    |
    v
ProductRepository
    |
    v
MySQL products table
```

## HTTP Method Colors In Swagger

- Blue `GET` means fetch/read data.
- Green `POST` means create new data.
- Orange `PUT` means update existing data.
- Red `DELETE` means delete data.

## What Is Try It Out?

`Try it out` allows us to send a real API request from the browser.

When we clicked `Execute` for `POST /api/products`, Swagger sent JSON data to Spring Boot. Spring Boot saved it in MySQL.

## What Is JSON?

JSON is the format used to send data to backend APIs.

Example:

```json
{
  "name": "Paracetamol",
  "company": "Cipla",
  "batchNumber": "CP101",
  "hsnCode": "300490",
  "manufactureDate": "2026-01-01",
  "expiryDate": "2027-01-01",
  "quantity": 100,
  "rate": 12.50,
  "gstPercentage": 12,
  "discountPercentage": 5
}
```

Spring Boot converts this JSON into a Java object called `ProductRequest`.

## Why Swagger Appears Automatically

We added this dependency in `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

This dependency scans controller classes.

When it sees:

```java
@PostMapping
```

it creates a POST API entry in Swagger.

When it sees:

```java
@GetMapping
```

it creates a GET API entry in Swagger.

## Interview Answer

Swagger is used for API documentation and testing. In my Spring Boot project, I used Springdoc OpenAPI. It scans controller classes and displays all endpoints in the browser. I can test APIs directly from Swagger by sending JSON requests and checking responses.

