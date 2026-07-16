# Medi-AI-Sales: Cloud ERP & GenAI Pharmacy Management System

Medi-AI-Sales is a secure, enterprise-grade pharmacy inventory, sales billing, and AI-assisted analytics system. Designed for medical stores and pharmacy operations, it integrates transactional stock bookkeeping with a localized Generative AI RAG engine.

---

## 🚀 Key Features

* **Smart Inventory Management:** Complete tracking of medicine categories, real-time stock levels, batch classifications, and low-stock warning triggers.
* **Instant PDF Invoicing:** Auto-calculates tax rates (GST) and custom discounts, compiling transactions into formatted invoices with automated PDF streaming via `OpenPDF`.
* **Generative AI RAG Assistant:** An integrated natural language assistant powered by `Spring AI` and a localized `Ollama` model (`tinyllama`). Users can query live database stock tables and inventory states in plain English.
* **Stateless Security & RBAC:** Complete API endpoint security configured via `Spring Security 6` and `JSON Web Tokens (JWT)`, establishing strict Role-Based Access Control (RBAC) for Admins and Operators.
* **Verification & OTP Services:** Coded custom registration and password recovery flows using the `Spring Boot Mail Starter` package for secure validation.
* **Premium User Dashboard:** A responsive dark mode React interface with glassmorphic cards, custom animations, active tab POS components, and interactive AI chat interfaces.

---

## 🛠️ Technology Stack

### Backend
* **Java 17 / Spring Boot 3** (Core Web App Engine)
* **Spring Data JPA & Hibernate** (ORM & Database Layer)
* **Spring Security 6 & JWT** (Stateless Token Authentication)
* **Spring AI** (Retrieval-Augmented Generation RAG Pipeline)
* **Spring Boot Mail** (SMTP Verification Codes)
* **JUnit 5 & Mockito** (Unit & Integration Testing)

### Frontend
* **React & Vite** (Single-Page UI Engine)
* **Vanilla CSS / Custom Glassmorphism** (Interface Aesthetics)
* **Lucide Icons** (Dashboard Graphics)

### Database & Cloud DevOps
* **Aiven.io** (Cloud Managed MySQL instance)
* **Render.com** (Dockerized Backend Web Service)
* **Vercel.com** (React Frontend Hosting)
* **GitHub Actions** (CI/CD automated deployment pipeline)

---

## 🌐 Deployed Cloud Architecture

The application is fully hosted in the cloud with an automated deployment pipeline:
* **Frontend:** Hosted on Vercel, pointing dynamically to the cloud API.
* **Backend:** Deployed on Render using a multi-stage Docker build to compile the source code directly on cloud nodes.
* **Database:** Hosted as a managed MySQL instance on Aiven.io.
* **CI/CD:** Pushing code changes to the GitHub `main` branch automatically triggers cloud rebuilds on Render and Vercel.

---

## 💻 Local Setup & Installation

### Prerequisites
* Java JDK 17
* Maven 3.x
* MySQL Database running locally
* Ollama running locally (for AI capabilities)

### Step-by-step Setup
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Bharathchandua/medi-ai-sales.git
   cd medi-ai-sales
   ```
2. **Configure Database Connection:**
   Edit the database URL, username, and password in `src/main/resources/application.properties` (or set environment variables `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`).
3. **Run the Spring Boot Backend:**
   ```bash
   mvn clean spring-boot:run
   ```
4. **Run the React Frontend:**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   Open `http://localhost:5173` in your browser.
