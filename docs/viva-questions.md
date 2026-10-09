# B.Sc. Computer Science Viva Questions & Answers (VSMS)

Comprehensive Viva Voce preparation guide for the **Vehicle Service Management System**.

---

### Q1: What is the Vehicle Service Management System (VSMS)?
**Answer:** VSMS is a full-stack web application designed for automobile workshops to automate vehicle onboarding, service bookings, mechanic job assignments, billing invoices, and vehicle maintenance history tracking.

---

### Q2: Explain the Three-Tier Architecture used in this project.
**Answer:** The architecture separates concerns into three distinct layers:
1. **Presentation Tier (Frontend):** Pure HTML5, CSS3, and JavaScript running in the user's browser, communicating via fetch requests.
2. **Application Tier (Backend):** Java Spring Boot REST API application containing controllers, business service rules, and data access repositories.
3. **Data Tier (Database):** Cloud-hosted MongoDB Atlas document store persisting collections as BSON documents.

---

### Q3: Why did you choose Java Spring Boot for the backend?
**Answer:** Spring Boot provides rapid application development features including embedded Apache Tomcat (no separate web server installation needed), automatic dependency injection (`@Autowired` / constructor injection), robust RESTful controller mapping, and Spring Data MongoDB which eliminates boilerplate database querying.

---

### Q4: Why did you choose MongoDB instead of MySQL or PostgreSQL?
**Answer:** 
1. Vehicles have flexible attributes (varying types, optional fuel types, service logs).
2. JSON/BSON document mapping translates naturally to JavaScript objects in the frontend and Java POJOs in the backend.
3. MongoDB Atlas provides cloud hosting with automated backups and connection strings without requiring local server setup.

---

### Q5: What is BCrypt and why is it used for passwords?
**Answer:** BCrypt is a secure, slow, adaptive hashing algorithm that uses a cryptographic salt to protect against rainbow table and brute-force dictionary attacks. In our project, `BCryptPasswordEncoder` hashes user passwords before saving them in the database. When logging in, `passwordEncoder.matches()` verifies the raw password against the stored hash. Plaintext passwords are never saved.

---

### Q6: Why did you avoid full Spring Security and use only `spring-security-crypto`?
**Answer:** Full Spring Security introduces complex filter chains, default session cookies, CSRF tokens, and automatic URL blocking that can easily cause 401/403 errors and over-complicate a 2nd-year college project. By including only `spring-security-crypto`, we obtain cryptographic BCrypt password hashing without unnecessary security configuration overhead.

---

### Q7: What is CORS and how did you configure it?
**Answer:** **Cross-Origin Resource Sharing (CORS)** is a browser security mechanism that blocks web pages from making AJAX requests to a different domain or port than the one that served the page. Since our frontend runs on ports like 3000 or 5500 (or Vercel) and the backend runs on port 8080, we implemented `CorsConfig.java` to explicitly allow cross-origin requests from configured origins.

---

### Q8: What is a REST API? Which HTTP methods did you use?
**Answer:** A Representational State Transfer (REST) API is a stateless architecture style for exchanging structured data over HTTP. We used standard HTTP methods:
- `GET`: Retrieve data (e.g. list vehicles, fetch dashboard stats).
- `POST`: Create new resources (e.g. register user, book service, create bill).
- `PUT`: Update existing resources (e.g. update status, assign mechanic).
- `DELETE`: Remove records (e.g. delete vehicle, delete mechanic).

---

### Q9: What is `ApiResponse<T>` and why is it beneficial?
**Answer:** `ApiResponse<T>` is a generic response wrapper with `{ success: boolean, message: string, data: T }`. This standardizes all API responses so the frontend can handle success and failure uniformly without crashing on unexpected payloads.

---

### Q10: How does Global Exception Handling work in your project?
**Answer:** We created `GlobalExceptionHandler.java` annotated with `@RestControllerAdvice`. It intercepts exceptions across all controllers (such as `ResourceNotFoundException`, validation failures, or database duplicate errors) and converts them into structured HTTP status codes and friendly JSON responses, preventing internal stack traces from leaking to the browser.

---

### Q11: How are human-readable identifiers like `SRV-0001` or `BILL-0001` generated?
**Answer:** In `ServiceRequestService` and `BillService`, synchronized counter methods count existing documents and format the number using `String.format("SRV-%04d", count + 1)`, ensuring collisions are prevented.

---

### Q12: How does the Service History get automatically created?
**Answer:** When an admin updates a service request's status to `"Completed"`, `ServiceRequestService.updateStatus()` checks if a history entry exists. If not, it copies the vehicle details, service type, complaint, mechanic name, and costs into the `service_history` collection and resets the assigned mechanic's availability to `"Available"`.

---

### Q13: Why can't a Spring Boot server run directly on Vercel?
**Answer:** Vercel is designed for static assets (HTML/CSS/JS) and short-lived Node.js/Python serverless functions. Spring Boot requires a persistent Java Virtual Machine (JVM) and an embedded Tomcat server running continuously on an open port. Therefore, the frontend is deployed to Vercel, while the backend is deployed to Java-compatible platforms like Render or Railway.

---

### Q14: How is user authentication maintained on the frontend?
**Answer:** When `/api/auth/login` verifies credentials, the backend returns the user object without the password. The frontend stores this user data in browser `localStorage` under the key `vsms_user`. The helper `requireAuth()` inspects this object to guard pages based on roles.

---

### Q15: What is the difference between `@Document` and `@Entity`?
**Answer:** `@Entity` is a JPA (Java Persistence API) annotation used for relational SQL databases (Hibernate, MySQL). `@Document` is a Spring Data MongoDB annotation indicating that the Java class maps to a MongoDB document collection.

---

### Q16: How do Spring Data MongoDB Repositories work without writing implementation classes?
**Answer:** Spring Data uses dynamic proxies. By creating an interface extending `MongoRepository<Vehicle, String>`, Spring Boot automatically generates bytecode implementations for standard CRUD methods (`save`, `findAll`, `findById`, `deleteById`) and parses custom method names (e.g., `findByVehicleNumber`) into database queries at startup.

---

### Q17: What are DTOs and why are they used?
**Answer:** **Data Transfer Objects (DTOs)** like `LoginRequest` and `RegisterRequest` carry data between the client and server. They decouple the external HTTP request structure from internal database entities and allow input validation using Jakarta annotations like `@Email` and `@NotBlank`.

---

### Q18: What validation checks are performed on input fields?
**Answer:**
- **Frontend:** Phone numbers must be 10 digits; emails must follow standard regex; required fields cannot be empty; service dates must be today or future.
- **Backend:** JSR-380 Jakarta annotations (`@NotBlank`, `@Email`, `@Size`, `@Pattern`) validate request bodies and reject invalid requests with HTTP 400.

---

### Q19: How are sensitive credentials like the MongoDB connection string protected?
**Answer:** The MongoDB connection string containing username and password is never committed to GitHub or hardcoded into frontend scripts. It is defined in `.env` files and referenced as `${MONGODB_URI}` in `application.properties`, loaded into the backend server environment at runtime.

---

### Q20: What happens if a customer tries to register with an already existing email?
**Answer:** The `AuthService` executes `userRepository.existsByEmail(email)`. If true, an `IllegalArgumentException` is thrown. The `GlobalExceptionHandler` catches this and returns a clear message with HTTP 400/409, which the frontend displays in an alert banner.

---

### Q21: What role does Maven play in this project?
**Answer:** Apache Maven is the build automation tool. Its `pom.xml` file specifies project dependencies (Spring Boot, MongoDB driver, Validation, BCrypt), manages versions, and compiles Java source files into executable JAR bytecode via `mvn clean compile` or `mvn spring-boot:run`.

---

### Q22: What future enhancements could be added to this system?
**Answer:**
1. Integration of online payment gateways (Razorpay, Stripe).
2. Automated SMS/WhatsApp notifications on service status updates.
3. Spare parts inventory tracking with low-stock alerts.
4. Customer feedback and star rating reviews after service completion.
