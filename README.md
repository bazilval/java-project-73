### Hexlet tests and linter status:
[![Actions Status](https://github.com/bazilval/java-project-73/workflows/hexlet-check/badge.svg)](https://github.com/bazilval/java-project-73/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/b05a89138a4a9a33a504/maintainability)](https://codeclimate.com/github/bazilval/java-project-73/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/b05a89138a4a9a33a504/test_coverage)](https://codeclimate.com/github/bazilval/java-project-73/test_coverage)

# Task Manager

Deploy: https://my-task-manager-9201.onrender.com

Swagger: https://my-task-manager-9201.onrender.com/swagger

## Overview
This project is a Spring Boot web application that provides APIs for managing various resources: Labels, Statuses, Tasks, and Users. Closest reference is http://www.redmine.org/.
It is implemented Spring Security 6 with JWT authentication and uses a stack of advanced technologies such as Liquibase, PostgreSQL and MapStruct.


## Development

``` bash
make test
```

``` bash
make backend
#http://localhost:5001
```

## Dependencies

The project uses various dependencies managed by Gradle. Key dependencies include:

-   Spring Boot and its various starters (web, data JPA, actuator, security)
-   JSON Web Tokens (JJWT)
-   PostgreSQL and H2 Database drivers
-   Liquibase for database migration
-   SpringDoc OpenAPI for API documentation
-   Hibernate Validator for bean validation
-   MapStruct for object mapping
-   Lombok for reducing boilerplate code
-   Rollbar error tracking

## APIs

The application exposes the following RESTful APIs:

### Label Management API

-   **GET /labels**: List all labels
-   **GET /labels/{id}**: Get a label by ID
-   **POST /labels**: Create a new label
-   **PUT /labels/{id}**: Update a label by ID
-   **DELETE /labels/{id}**: Delete a label by ID

### Status Management API

-   **GET /statuses**: List all statuses
-   **GET /statuses/{id}**: Get a status by ID
-   **POST /statuses**: Create a new status
-   **PUT /statuses/{id}**: Update a status by ID
-   **DELETE /statuses/{id}**: Delete a status by ID

### Task Management API

-   **GET /tasks**: List all tasks.
```
It is also possible to make filtration of results.
Add one or few request params: *taskStatus*, *authorId*, *executorId*, *labelsId*.
For example: GET /tasks?labelsId=1&authorId=10 will return tasks that were created by User with id=10 and that has Label with id=1
```
-   **GET /tasks/{id}**: Get a task by ID
-   **POST /tasks**: Create a new task
-   **PUT /tasks/{id}**: Update a task by ID
-   **DELETE /tasks/{id}**: Delete a task by ID

### User Management API

-   **GET /users**: List all users
-   **GET /users/{id}**: Get a user by ID
-   **POST /users**: Create a new user
-   **PUT /users/{id}**: Update a user by ID
-   **DELETE /users/{id}**: Delete a user by ID