# Library Automation System

This project is developed using Java 17, Spring Boot 3, and Maven. It utilizes H2 database and can be run on Docker.

## Requirements

- Docker: The project is containerized with Docker. If you don't have Docker installed, you can download it from [here](https://docs.docker.com/get-docker/).

## Running with Docker

Ensure Docker is installed and running on your machine. Then, you can run the project on Docker using the following command:

```bash
docker-compose up
```

## Swagger

The project utilizes Swagger for API documentation. You can access the Swagger UI via the following URL:

[Swagger UI](http://localhost:2703/swagger-ui/index.html)

## H2 Database Console

You can access the H2 Database Console via the following URL:

[http://localhost:2703/h2-console](http://localhost:2703/h2-console)
The JDBC URL for the H2 database can be found in the [`application-demo.properties`](src/main/resources/application-demo.properties) file.

## Project Features

- Addition, deletion, updating, and listing of books in the library.
- Registration of new users in the system with user roles.
- Ability for administrators and librarians to add users to the system.
- Management of shelves where books are stored, including entry and updating of shelf information by administrators.
- Ability for users to borrow books and application of a daily penalty fee if the borrowed book is returned late; the penalty fee is collected by the librarian upon book return.
- Viewing of previously borrowed books by users, with access granted to librarians and administrators.
- Ability for users to leave comments on books, with comments visible to all users.
- Creation of reading lists for users to keep track of books they wish to read.
- Ability for users to make reservations for books.