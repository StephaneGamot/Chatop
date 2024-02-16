# ChâTop

## Description
ChâTop is a real estate rental management application built with Spring and Java.

## Software Tools
Before you start, ensure you have the following tools installed:
- **Java 17**: [Download JDK](https://www.oracle.com/java/technologies/downloads/)
- **Spring Framework**: [Start with Spring](https://start.spring.io)
- **Maven**: To build and manage the project.
- **SQL with MySQL**: [Configure MySQL](https://dev.mysql.com/downloads/installer/). Run initialization scripts in `/sql`.
- **Angular**: [Install Angular](https://angular.io). Launch the front-end with `ng serve`.

## Getting Started
- Clone the backend repository: `git clone https://github.com/White-Wolf-Web/chatop-backend.git`
- Import the project into your IDE and build it using Maven.

## Database Configuration
- Create a `.env` file at your project root.
- Set the MySQL environment variable: `MY_SQL_PASSWORD=XXXX`.
- Create a new database in MySQL: `mysql -u username -p` and then `CREATE DATABASE chatop_db`.

## Security
Set up your JWT secret key: `JWT_SECRET_KEY=XXXX`.

## Architecture Overview
- **Configuration**: Holds all application-wide configuration files.
- **Controller**: Handles incoming HTTP requests and directs them to the service layer.
- **Service**: Defines the business logic interface.
- **ServiceImpl**: Concrete implementation of the service interface.
- **Models**: Represents the application's data structures.
- **DTO**: Data Transfer Objects for transferring data across processes.
- **Repository**: Interface for database operations.
- **Security**: Configurations related to application security, including JWT setup.

## API Documentation
Access the Swagger UI for the API documentation at `http://localhost:3001/swagger-ui/index.html`.

## API Endpoints
Here are some of the main endpoints of the ChâTop API:

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
To start the application, run the Spring Boot project with `mvn spring-boot:run`.

## Author
Stéphane Gamot

 
