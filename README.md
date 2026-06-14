# Java HTTP Server from Scratch

A lightweight, non‑blocking HTTP server built with **pure Java** (no external dependencies).  

## Features

- **Pure Java 21+** – virtual threads for high concurrency (`Executors.newVirtualThreadPerTaskExecutor()`).
- **Annotation‑driven controllers** – `@Controller`, `@RequestMapping` with path parameters (e.g., `/users/{id}`).
- **Custom JSON parser** – supports objects, strings, numbers.
- **HTTP/1.1 compliant** – request line, headers, body (`Content-Length`).
- **Gzip compression** – when client sends `Accept-Encoding: gzip`.
- **File serving & upload** – endpoints `/files/{file}` (GET / POST).
- **Interceptor mechanism** – modify responses before they are sent.
- **Clean error handling** – proper HTTP status codes (400, 404, 500).

## Getting Started

### Prerequisites

- **Java 21** or later
- **Maven**

### Build & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/AidarSarvartdinov/http-server-in-pure-Java.git
   cd http-server-in-pure-Java/http
   ```

2. **Build with Maven**
   ```bash
   mvn clean package
   ```
3. **Run the Server**
   ```bash
   java -jar target/java-http-server-1.0-SNAPSHOT.jar
   ```
   By default the server listens on port 8080 and serves files only if --directory is provided.

### Command-line Arguments
| Argument     | Description                                    | Default |
|--------------|------------------------------------------------|---------|
| `--port`     | TCP port number                                | `8080`  |
| `--directory`| Directory for file uploads/downloads           | none (disables file endpoints) |

### Testing with cURL
```bash
# GET request
curl -v http://localhost:8080/echo/hello

# POST JSON
curl -X POST http://localhost:8080/json/echo -d '{"key":"value"}' -H "Content-Type: application/json"

# File upload (POST)
curl -X POST http://localhost:8080/files/notes.txt -d "hello world"

# Download file
curl http://localhost:8080/files/notes.txt
```