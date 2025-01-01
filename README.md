# Secure API Gateway Project

This project is a secure and scalable API gateway for routing and securing requests built with Spring Boot, featuring JWT authentication, rate limiting with Bucket4j, role-based access, and data encryption. Performance was optimized using Redis caching and sensitive data is securely encrypted using BCrypt. Unit test coverage was added using MockMvc and JUnit.

## Features

- **JWT-Based Authentication**: Secures application endpoints by using JSON Web Tokens for user authentication and protects specific routes based on user roles.
- **Refresh Token Mechanism**: Extends session validity without requiring frequent logins by adding a `/refresh-token` endpoint that generates a new JWT based on a valid refresh token.
- **Rate Limiting**: Prevents abuse by limiting the number of requests a user can make within a specified time frame using a custom filter implemented with the Bucket4j library.
- **Role-Based Authorization**: Implements role-based access control (RBAC) by assigning roles (e.g., `ADMIN`, `USER`) and restricting actions based on roles, enhanced with `@PreAuthorize` annotations.
- **Caching Layer**: Improves response times by caching frequently accessed data using Redis.
- **Global Error Handling: Centralizes exception handling with `@ControllerAdvice` and `@ExceptionHandler` for better error management.
- **Request and Response Validation**: Ensures that incoming requests and outgoing responses adhere to a schema using DTO validation annotations like `@NotNull`, and `@Size`.
- **Input Sanitization**: Validates and sanitizes request payloads with annotations like `@Valid` and sanitizing input fields to prevent SQL injection and XSS attacks.
- **Encryption of Sensitive Data**: Encrypts sensitive fields like passwords or tokens before storing them using `BCryptPasswordEncoder` in the service.
- **Password Encryption**: Encrypts sensitive password fields using `BCryptPasswordEncoder`.
- **User Registration and Login**: Provides endpoints for user registration and login, handling password hashing and JWT generation.
- **Post Management**: Manages CRUD (Create, Read, Update, and Delete) operations for posts, ensuring that only authenticated users can create, update, or delete posts.

## Tools and Technologies

- **Java 17** and **Spring Boot**: Framework for building the API Gateway.
- **JWT (JSON Web Tokens)**: For secure authentication and authorization.
- **MySQL**: Used to store user accounts and post data.
- **MockMvc**: For testing APIs and validating features.
- **JUnit 5**: For unit and integration testing.
- **Maven**: For dependency management and project build automation.
- **Bucket4j**: Library used for implementing custom rate limiting.
- **Redis**: Caching solution to improve response times by caching frequently accessed data.
- **Spring Data JPA**: For database interactions using the repository pattern.
- **BCryptPasswordEncoder**: For encrypting sensitive password fields.
- **Hibernate Validator**: For input validation and sanitization.
