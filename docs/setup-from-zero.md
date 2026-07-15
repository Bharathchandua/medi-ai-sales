# Setup From Zero

This guide explains how to open, understand, and run this project from IntelliJ IDEA.

## 1. What Is Codex And What Is IntelliJ?

Codex and IntelliJ are working on the same files in your computer.

- Codex helps create, edit, explain, and review code.
- IntelliJ is where you open the project, run the application, debug, and use Maven tools.

The project folder is:

```text
C:\Users\User\IdeaProjects\Spring\medi-sales\medi-ai-sales
```

When Codex creates a file, you can see the same file in IntelliJ after opening this folder.

## 2. Which Folder Should You Open In IntelliJ?

Open this exact folder:

```text
C:\Users\User\IdeaProjects\Spring\medi-sales\medi-ai-sales
```

Do not open only `src`.
Do not open the old `medi-sales` folder for this new project.

The correct folder contains:

```text
pom.xml
README.md
src
docs
```

## 3. What Is `pom.xml`?

`pom.xml` is the Maven configuration file.

Maven is a Java build tool. It helps with:

- downloading dependencies
- compiling code
- running tests
- packaging the application
- starting Spring Boot

Example:

If we need Spring Web, we do not manually download jar files. We add this dependency in `pom.xml`, and Maven downloads it.

Interview answer:

> `pom.xml` is the Maven project configuration file. It defines project metadata, Java version, dependencies, and build plugins.

## 4. What Are Dependencies?

Dependencies are external libraries used by the project.

In this project:

- `spring-boot-starter-web` is used to build REST APIs.
- `spring-boot-starter-data-jpa` is used to connect Java objects with database tables.
- `spring-boot-starter-validation` is used for request validation.
- `mysql-connector-j` is used to connect Spring Boot with MySQL.
- `springdoc-openapi` is used for Swagger API documentation.

Interview answer:

> Dependencies are libraries required by the application. Maven manages them through `pom.xml`.

## 5. What Is `application.properties`?

`application.properties` is the Spring Boot configuration file.

It is used for settings like:

- application name
- server port
- database URL
- database username
- database password
- JPA/Hibernate settings
- Swagger settings

In this project it is here:

```text
src/main/resources/application.properties
```

Interview answer:

> `application.properties` stores external configuration for the Spring Boot application, such as database connection details, server port, and JPA settings.

## 6. Why We Do Not Hardcode Passwords

In the old project, the database password was written directly in `application.properties`.

In this new project, we use environment variables:

```properties
spring.datasource.password=${DB_PASSWORD:}
```

This means:

- if `DB_PASSWORD` is available, use it
- otherwise use empty password as default

Interview answer:

> Secrets like database passwords should not be hardcoded because they can leak through GitHub. Environment variables are safer.

## 7. What Is The Main Class?

The main class starts the Spring Boot application:

```text
src/main/java/com/medi/ai/sales/MediAiSalesApplication.java
```

It contains:

```java
SpringApplication.run(MediAiSalesApplication.class, args);
```

Interview answer:

> The main class is the entry point of the Spring Boot application. It starts the embedded server and loads Spring beans.

## 8. How To Run In IntelliJ

1. Open IntelliJ IDEA.
2. Click `File`.
3. Click `Open`.
4. Select:

```text
C:\Users\User\IdeaProjects\Spring\medi-sales\medi-ai-sales
```

5. Wait for Maven indexing to finish.
6. Open:

```text
src/main/java/com/medi/ai/sales/MediAiSalesApplication.java
```

7. Click the green run button near the `main` method.

If everything is configured, Spring Boot starts on:

```text
http://localhost:8080
```

Swagger will be available at:

```text
http://localhost:8080/swagger-ui.html
```

## 9. MySQL Setup Needed

Before running with MySQL, create a database:

```sql
CREATE DATABASE medi_ai_sales;
```

Then set these environment variables in IntelliJ Run Configuration:

```text
DB_URL=jdbc:mysql://localhost:3306/medi_ai_sales
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

## 10. What If Maven Is Not Installed?

Your terminal currently says `mvn` is not recognized.

That means Maven is not available in the terminal PATH.

But IntelliJ usually has bundled Maven support. So first try running from IntelliJ.

Later, install Maven separately so commands like this work in terminal:

```text
mvn test
```

## 11. First API To Test

After the project runs, open Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Use `POST /api/products` with this JSON:

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

## 12. What You Should Understand Today

Do not try to memorize everything.

Understand this:

```text
pom.xml -> tells Maven what technologies we use
application.properties -> tells Spring Boot how to run
Main class -> starts the app
Controller -> receives API request
Service -> contains logic
Repository -> talks to database
Entity -> maps to database table
DTO -> request/response object
```

