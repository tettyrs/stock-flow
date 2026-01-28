# Read Me First
The following guides illustrate how to use some features concretely:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.3.0/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/#data.sql.jpa-and-spring-data-jpa)

## Environment Variables
This project uses a `.env` file for configuration. Please ensure you have it in the root directory.

Example `.env` content:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=project_db  # Can be changed to any database name
DB_USERNAME=root
DB_PASSWORD=secret
```
The application uses `spring-dotenv` to load these variables.

## Running the Application
You can run the application using the following command:

```bash
./mvnw spring-boot:run
```

## Testing
You can run the tests using the following command:

```bash
./mvnw test
```

## API Testing
You can import `postman_collection.json` into Postman/Insomnia.
- Set the `baseUrl` variable to `http://localhost:8080`.
