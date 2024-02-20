# ChâTop-BackEnd

## Description
ChâTop is a sophisticated real estate rental management application that leverages the robustness of Spring and the agility of Java for backend operations.

## Software Tools
Before you start, ensure you have the following tools installed:
- **Java 17**: [Download JDK](https://oracle.com/java/technologies/javase-jdk17-downloads.html)
- **Spring Framework**: Get started with [Spring](https://spring.io/start)
- **Maven**: Essential for building and managing the project.
- **SQL with MySQL**: Set up your MySQL and execute initialization scripts located in `/sql`.
- **Angular**: Install [Angular CLI](https://angular.io/cli) and start the frontend with `ng serve`.

## Getting Started
1. Clone the backend repository:
   git clone https://github.com/White-Wolf-Web/chatop-backend.git
2. Import the project into your IDE.
3. Build the project with Maven: mvn clean install

## Database Configuration
1. At the root of your project, create a `.env` file.
2. Define the MySQL environment variable: `MY_SQL_PASSWORD=XXXX`.
3. Initialize your MySQL database: CREATE DATABASE chatop_db;

## Security
Set up your JWT secret key: `JWT_SECRET_KEY=XXXX`.

## Architecture Overview
- **Configuration**: Centralizes all application-wide configurations.
- **Controller**: Manages incoming HTTP requests and routes them to the service layer.
- **Service**: Interface that outlines business logic.
- **ServiceImpl**: Provides the concrete implementation of the service interface.
- **Models**: Defines the core data structures of the application.
- **DTO**: Facilitates data transfer across different layers.
- **Repository**: Manages database interactions.
- **Security**: Contains security configurations, including JWT handling.

## API Documentation
Explore our interactive API documentation via Swagger UI at [http://localhost:3001/swagger-ui/index.html](http://localhost:3001/swagger-ui/index.html).

## API Endpoints
Access the core functionalities of ChâTop through these API endpoints:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/rentals`
- `GET /api/rentals/{id}`
- `POST /api/rentals`
- `PUT /api/rentals/{id}`
- `GET /api/user/{id}`
- `POST /api/messages`

## Running the Project
Launch the application by executing: mvn spring-boot:run

## Author
Developed with 💻 and ❤ by Stéphane Gamot


