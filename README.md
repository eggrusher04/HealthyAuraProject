# HealthyAura — Full-Stack Healthy Food Finder

HealthyAura is a **full-stack web application** that helps users discover affordable healthy eateries, view recommendations, manage profiles, and redeem health rewards.

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
2) Open "HealthyAura/backend/src/main/resources/application.properties" and change the values to match your MySQL setup
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

