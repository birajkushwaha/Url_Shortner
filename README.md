# URL Shortener Backend

A high-performance, lightweight URL Shortener backend built using **Spring Boot 3**, **Java 17**, and **Spring Data JPA** with a persistent **H2 Database**.

---

## Features

- **URL Shortening**: Generates secure, random 7-character alphanumeric keys (Base62) for long URLs.
- **Redirection**: Redirects clients from short links to original URLs with `302 Found` status.
- **Click Analytics**: Counts and tracks the number of times a short URL is clicked.
- **Database Persistence**: Local file-based H2 database storage so mappings are not lost on application restart.
- **Interactive Console**: Built-in H2 Web Console to inspect and manage database tables.

---

## Technology Stack

- **Framework**: Spring Boot 3.3.0 (Web, Data JPA, Validation)
- **Language**: Java 17
- **Database**: H2 Database (File-based)
- **Build Tool**: Maven (with Maven Wrapper)

---

## API Endpoints

### 1. Shorten a URL
Creates a shortened key for a given original URL.
- **Endpoint**: `POST /api/v1/urls/shorten`
- **Request Body**:
  ```json
  {
    "originalUrl": "https://www.google.com/search?q=spring+boot+java"
  }
  ```
- **Response Body (201 Created)**:
  ```json
  {
    "originalUrl": "https://www.google.com/search?q=spring+boot+java",
    "shortUrl": "http://localhost:8080/aB3d9Fx",
    "shortKey": "aB3d9Fx",
    "createdAt": "2026-07-01T19:00:00",
    "clicksCount": 0
  }
  ```

### 2. Redirect to Original URL
Redirects client to the original URL associated with the short key and increments the clicks count.
- **Endpoint**: `GET /{shortKey}`
- **Response**: `302 Found` with `Location` header redirecting to the original URL. If the key is not found, returns `404 Not Found`.

### 3. Get URL Analytics / Info
Retrieves analytics information (including click count) for a short URL.
- **Endpoint**: `GET /api/v1/urls/{shortKey}/analytics`
- **Response Body (200 OK)**:
  ```json
  {
    "originalUrl": "https://www.google.com/search?q=spring+boot+java",
    "shortUrl": "http://localhost:8080/aB3d9Fx",
    "shortKey": "aB3d9Fx",
    "createdAt": "2026-07-01T19:00:00",
    "clicksCount": 15
  }
  ```

---

## How to Run the Application

### Prerequisites
- Java 17 or higher installed on your system.

### Build and Run
Use the Maven Wrapper included in the repository to start the server:

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**On Linux/macOS:**
```bash
./mvnw spring-boot:run
```

The application will start on port `8080` (http://localhost:8080).

---

## Database Management & H2 Console

The project uses H2 file database persistence. You can inspect the tables using the embedded console:

1. Open your browser and go to `http://localhost:8080/h2-console`.
2. Configure the connection fields as follows:
   - **Saved Settings**: Generic H2 (Embedded)
   - **JDBC URL**: `jdbc:h2:file:./data/urlshortener`
   - **User Name**: `sa`
   - **Password**: *(Leave blank)*
3. Click **Connect** to query the `url_mappings` table.
