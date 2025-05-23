# Chef App

A Spring Boot-based web application that allows users to explore various dishes and ingredients, manage their recipes, and more. This project implements various features including a dish catalog, ingredient filtering, and CRUD operations for dishes and ingredients.

## Features

- **Dish Management**: View, create, update, and delete dishes.
- **Ingredient Management**: Browse and filter ingredients by category.
- **Ingredient Category Filtering**: Filter ingredients by their category (e.g., Vegetables, Fruits, etc.).
- **Dish Details**: View detailed information for each dish.
- **Pagination**: All lists (dishes and ingredients) are paginated for better user experience.
- **Admin Panel**: Admin functionalities for managing dishes and ingredients.
- **Logging**: AOP-based logging to track service and repository method calls.

## Technologies Used

- **Backend**:
    - Spring Boot
    - Spring MVC
    - Spring Data JPA
    - Thymeleaf
    - Hibernate
    - JUnit 5 for testing
    - AOP for logging

- **Frontend**:
    - Thymeleaf templates for rendering HTML pages
    - CSS and Bootstrap for basic styling

- **Database**:
    - H2 (In-memory database for testing/development)

## Setup

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/chef-app.git
    ```

2. Navigate to the project directory:
    ```bash
    cd chef-app
    ```

3. Build the project using Maven:
    ```bash
    mvn clean install
    ```

4. Run the application:
    ```bash
    mvn spring-boot:run
    ```

   By default, the app will be available at `http://localhost:8080`.

## Project Structure

- **`src/main/java/com/maksy/chefapp/`**:
    - `aspect/`: Contains the AOP functionality for logging method calls.
    - `controller/`: Contains controllers to handle HTTP requests.
    - `service/`: Contains service classes implementing the business logic.
    - `model/`: Contains the domain entities.
    - `repository/`: Contains the JPA repositories for interacting with the database.
    - `dto/`: Contains Data Transfer Objects for data mapping.
    - `mapper/`: Contains mappers to convert between entities and DTOs.
    - `exception/`: Contains custom exception classes.

- **`src/main/resources/`**:
    - **`templates/`**: Contains Thymeleaf templates (e.g., `dishes.html`, `ingredients.html`, etc.).
    - **`static/`**: Contains static files like CSS and JavaScript.
    - **`application.properties`**: Configuration file for application settings (e.g., database, logging).

- **`src/test/java/com/maksy/chefapp/`**:
    - Contains unit tests for controllers, services, and other components.

## Endpoints

- **`/dishes`**: Displays a list of all dishes, with pagination and optional filtering by dish type.
- **`/dishes/{id}`**: Displays detailed information about a specific dish.
- **`/dishes/delete/{id}`**: Deletes a specific dish.
- **`/dishes/edit/{id}`**: Edits the details of a dish.
- **`/dishes/save`**: Adds a new dish to the database.
- **`/ingredients`**: Displays a list of all ingredients with pagination and optional filtering by category.
- **`/about`**: A simple about page.

## Testing

- **Unit Tests**: The application includes unit tests for controllers, services, and repositories. You can run the tests with:
    ```bash
    mvn test
    ```

- **MockMvc**: Used for testing the controller methods.

## AOP Logging

This project implements Aspect-Oriented Programming (AOP) to log method calls in services and repositories. This allows for monitoring the flow of the application, including method inputs and outputs.

## Contribution

Feel free to fork the project and submit pull requests with your contributions! 

