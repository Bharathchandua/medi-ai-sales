# SQL From Zero

SQL is the language used to talk to relational databases like MySQL.

For Java backend interviews and projects, you do not need to know every advanced SQL topic on day one. But you must understand basic database operations.

## Why A Java Developer Needs SQL

Spring Boot backend applications usually store data in databases.

Examples from this project:

- products are stored in the `products` table
- customers will be stored in a `customers` table
- invoices will be stored in an `invoices` table

Even though Spring Data JPA reduces manual SQL, you should still know SQL because interviewers ask it and real debugging often needs it.

## Database, Table, Row, Column

Think of a database like an Excel workbook.

- Database = workbook
- Table = sheet
- Column = field/header
- Row = one record

Example:

Database:

```text
medi_ai_sales
```

Table:

```text
products
```

Columns:

```text
id, name, company, quantity, rate
```

Row:

```text
1, Paracetamol, Cipla, 100, 12.50
```

## Create Database

```sql
CREATE DATABASE medi_ai_sales;
```

Meaning:

> Create a new database named `medi_ai_sales`.

## Show Databases

```sql
SHOW DATABASES;
```

Meaning:

> Show all databases available in MySQL.

## Use Database

```sql
USE medi_ai_sales;
```

Meaning:

> Select `medi_ai_sales` as the current database.

## Show Tables

```sql
SHOW TABLES;
```

Meaning:

> Show tables inside the selected database.

## Select All Rows

```sql
SELECT * FROM products;
```

Meaning:

> Get all columns and all rows from the `products` table.

## Select Specific Columns

```sql
SELECT id, name, quantity FROM products;
```

Meaning:

> Get only id, name, and quantity from products.

## Filter Rows

```sql
SELECT * FROM products WHERE quantity < 10;
```

Meaning:

> Get products whose quantity is less than 10.

## Insert Row

Normally our Spring Boot app inserts data using API. But SQL insert looks like this:

```sql
INSERT INTO products (name, company, quantity, rate)
VALUES ('Paracetamol', 'Cipla', 100, 12.50);
```

Meaning:

> Add one product row.

## Update Row

```sql
UPDATE products
SET quantity = 80
WHERE id = 1;
```

Meaning:

> Change quantity to 80 for product id 1.

## Delete Row

```sql
DELETE FROM products WHERE id = 1;
```

Meaning:

> Delete product with id 1.

## Important Interview Warning

Never run delete without `WHERE` unless you really want to delete all rows:

```sql
DELETE FROM products;
```

This deletes every product.

## What To Learn First

For now, learn these only:

- `CREATE DATABASE`
- `SHOW DATABASES`
- `USE`
- `SHOW TABLES`
- `SELECT`
- `WHERE`
- `INSERT`
- `UPDATE`
- `DELETE`
- `ORDER BY`
- `COUNT`
- `GROUP BY`
- `JOIN`

We will learn them while building this project.

