# HealthyAura — Full-Stack Healthy Food Finder

HealthyAura is a **full-stack web application** that helps users discover affordable healthy eateries, view recommendations, manage profiles, and redeem health rewards.
Note: The application resides in Lab3
---

## Features

- View healthy eateries and personalized recommendations  
- Interactive map (OneMap integration) for directions  
- User authentication (JWT) and profile management  
- Reward redemption with points tracking  
- Modular backend with Spring Boot and MySQL  
- Responsive React frontend with TailwindCSS

---

## Prerequisites
- Java Version 24 and above (required for Spring Boot 3+)
- Maven Version 3.8 and above (for dependency management)
- MySQL Version 8.0 and above (for database)
- Node.js Version 18 and above (for React frontend)
- Latest npm version (install dependencies)

---

## Frontend Setup (React)
1) npm install
2) npm start
3) Head to "http://localhost:3000"

---

## Backend Setup (Spring Boot)
1) Create a new database - "CREATE DATABASE healthyauradb;"
2) Ensure to make your own .env file in Lab3\backend\.env and follow the following template
```
# ==========================
# Application Configuration
# ==========================
SPRING_APPLICATION_NAME=HealthyAura
SERVER_PORT=8080

# ==========================
# Database Configuration
# ==========================
DB_URL=jdbc:<your mysql db url>
DB_USERNAME=<your username>
DB_PASSWORD=<your password>
DB_DRIVER=com.mysql.cj.jdbc.Driver

# ==========================
# JWT Configuration
# ==========================
JWT_SECRET=<your JWT generated secret>
JWT_EXPIRATION=3600000  # 1 hour in milliseconds

# ==========================
# Hibernate Configuration
# ==========================
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
HIBERNATE_DDL_AUTO=update
SHOW_SQL=true
FORMAT_SQL=true
```

4) Open "HealthyAura/backend/src/main/resources/application.properties" and ensure your .env matches the configuration
```
spring.datasource.url=jdbc:mysql://localhost:3306/healthyauradb
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

```

3) Open command prompt or your preferred terminal in the backend folder and execute "mvn spring-boot:run"
4) Once it is compiled, the backend will start on "http://localhost:8080"

---
## Example Login Flow

1) Sign up via /auth/signup
2) Log in via /auth/login — JWT token saved locally
3) Access home page (/home) for personalized recommendations
4) Explore eateries via /explore
5) Edit profile preferences at /profile
6) Check & redeem rewards at /rewards

---
## Folder Structure
<img width="440" height="406" alt="image" src="https://github.com/user-attachments/assets/e8441ece-7d45-439e-af80-f0ab46ef0336" />

---
## Detailed testing documentation
This google doc is to show different tests run and the full application flow tests
https://docs.google.com/document/d/1GaOReZVyQn5gaxL6rKZyOU3mg1lsy0TetvCA2Y2N6_8/edit?usp=sharing

---

