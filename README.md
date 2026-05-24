# Spring Backend - Long Polling Bakery

This Spring Boot backend exposes a long-polling endpoint that simulates a bakery order process.

## What This Service Does

- Exposes `GET /api/bake/{bakedGood}`.
- Uses `DeferredResult` to keep the HTTP request open while work is done asynchronously.
- Simulates bake time with a random delay between 1 and 10 seconds.
- Uses a 5-second long-poll timeout.
- Returns JSON with a `message` field for both success and timeout/error cases.

## Tech Stack

- Java 25 target (`pom.xml`)
- Spring Boot 4.0.0
- Spring Web (MVC)
- Spring WebFlux dependency present (not required for this controller flow)
- Spring Data JPA + H2 (currently no repository/entities used)
- Maven Wrapper (`./mvnw`, Maven 3.9.11)

## Main Backend Files

- `src/main/java/hac/longpolling/LongPollingApplication.java` - app entry point
- `src/main/java/hac/longpolling/BakeryController.java` - long-polling API
- `src/main/resources/application.properties` - currently empty
- `src/test/java/hac/longpolling/LongPollingApplicationTests.java` - context load test

## API

### `GET /api/bake/{bakedGood}`

Starts a simulated bake operation in a background thread pool (5 worker threads).

Possible outcomes:

- **Success (HTTP 200)** when bake completes before timeout
- **Timeout/Error response** when request exceeds long-poll timeout or async processing fails

Example response body:

```json
{
  "message": "Bake for cookie complete and order dispatched in 2389ms. Enjoy!"
}
```

Timeout-style response body:

```json
{
  "message": "the bakery is not responding in allowed time"
}
```

## Run Locally

From the project root:

```bash
cd /Users/solangekarsenty/WebstormProjects/React-Spring/07/long-polling
./mvnw spring-boot:run
```

The backend listens on `http://localhost:8080` by default.

## Try the Endpoint

```bash
curl -i http://localhost:8080/api/bake/cookie
curl -i http://localhost:8080/api/bake/baguette
```

Because bake time is randomized, repeated calls may return either a success message or timeout-style message.

## Run Tests

```bash
cd /Users/solangekarsenty/WebstormProjects/React-Spring/07/long-polling
./mvnw test
```

## Frontend Integration Note

If you run the Vite client in `react-client`, it proxies `/api/*` requests to `http://localhost:8080` during development. Keep backend and frontend running together for the full demo.

