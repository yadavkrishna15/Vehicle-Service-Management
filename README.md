# Vehicle Service Management System (VSMS)

A simple, complete, and robust Vehicle Service Management System built for a **B.Sc. Computer Science 2nd-Year College Project**.

---

## 1. Project Objective
Automobile service centers often rely on paper registers or scattered spreadsheets to record service bookings, track mechanic assignments, generate customer invoices, and maintain repair histories. 

The **Vehicle Service Management System (VSMS)** solves this by providing:
- A user-friendly digital interface for vehicle owners (customers) to register their vehicles and schedule service appointments.
- A centralized workshop dashboard for service managers (admins) to track incoming requests, assign available mechanics, track repair stages, compute billing, and archive service logs.

---

## 2. Key Features

### 👤 Customer Features
- **Account Registration & Authentication:** Secure sign up and login with BCrypt encrypted passwords.
- **Vehicle Registration:** Add and manage vehicles (Car, Bike, Scooter, SUV) with registration plate, brand, model, and fuel type.
- **Service Booking:** Book service appointments with preferred dates, service categories (General Service, Oil Change, Brake Service, Engine Check, etc.), and problem descriptions.
- **Track Active Services:** Real-time visibility into booking status (`Requested` ➔ `Confirmed` ➔ `In Service` ➔ `Completed`).
- **View Invoices & Bills:** Inspect itemized repair bills with labor, parts, and total amounts.
- **Vehicle Maintenance History:** Look up chronological history of past services and costs for each vehicle.

### 🛡️ Admin & Service Staff Features
- **Workshop Analytics Dashboard:** Instant metrics for total customers, vehicles, total bookings, pending jobs, in-progress jobs, completed services, and total invoices.
- **Customer Directory:** View customer contact details and search by name.
- **Fleet Inventory:** View all vehicles registered across customers with quick search.
- **Service Request Lifecycle Management:**
  - Update status (`Requested`, `Confirmed`, `In Service`, `Completed`, `Cancelled`).
  - Assign technicians and specialized mechanics.
  - Record estimated and final repair costs.
- **Automated Service History:** Setting a service request to `Completed` automatically logs an immutable record into `service_history` and releases the assigned mechanic back to `Available`.
- **Mechanic Staff Management:** Add, edit, remove mechanics, set specializations, and toggle availability (`Available` / `Busy`).
- **Billing & Invoice Generation:** Generate itemized bills (labor, parts, other), automatically calculate totals, mark payments (`Pending` / `Paid`), and print professional invoices.

---

## 3. Technology Stack

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Backend Language** | Java 17 (LTS) / 21 | Standard object-oriented enterprise language |
| **Backend Framework**| Spring Boot 3.2.x | REST API controllers, Spring Data MongoDB |
| **Build Tool** | Apache Maven 3.9+ | Standard dependency management and compilation |
| **Security / Crypto** | BCrypt (`spring-security-crypto`) | Robust password hashing (No full Spring Security overhead) |
| **Database** | MongoDB Atlas | Cloud-hosted NoSQL document database |
| **Frontend** | HTML5, Vanilla CSS3, Vanilla JS | Clean responsive UI without heavy JS frameworks |
| **Frontend Deployment**| Vercel | Fast static edge hosting |
| **Backend Deployment** | Render / Railway / Fly.io | Java-compatible PaaS container hosting |

> **College Project Simplicity Guarantee:** No Docker, Kubernetes, microservices, AI/ML, blockchain, complex payment gateways, WebSockets, or JWT token configs are used. Everything is clean, readable, and easy to explain.

---

## 4. Project Folder Structure

```text
vehicle-service-management-system/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/vsms/
│   │       │   ├── VsmsApplication.java
│   │       │   ├── config/
│   │       │   │   └── CorsConfig.java
│   │       │   ├── controller/
│   │       │   │   ├── AuthController.java
│   │       │   │   ├── CustomerController.java
│   │       │   │   ├── VehicleController.java
│   │       │   │   ├── ServiceRequestController.java
│   │       │   │   ├── MechanicController.java
│   │       │   │   ├── BillController.java
│   │       │   │   ├── ServiceHistoryController.java
│   │       │   │   └── DashboardController.java
│   │       │   ├── model/
│   │       │   │   ├── User.java
│   │       │   │   ├── Vehicle.java
│   │       │   │   ├── ServiceRequest.java
│   │       │   │   ├── Mechanic.java
│   │       │   │   ├── Bill.java
│   │       │   │   └── ServiceHistory.java
│   │       │   ├── repository/
│   │       │   │   ├── UserRepository.java
│   │       │   │   ├── VehicleRepository.java
│   │       │   │   ├── ServiceRequestRepository.java
│   │       │   │   ├── MechanicRepository.java
│   │       │   │   ├── BillRepository.java
│   │       │   │   └── ServiceHistoryRepository.java
│   │       │   ├── service/
│   │       │   │   ├── AuthService.java
│   │       │   │   ├── CustomerService.java
│   │       │   │   ├── VehicleService.java
│   │       │   │   ├── ServiceRequestService.java
│   │       │   │   ├── MechanicService.java
│   │       │   │   ├── BillService.java
│   │       │   │   ├── ServiceHistoryService.java
│   │       │   │   └── DashboardService.java
│   │       │   ├── dto/
│   │       │   │   ├── LoginRequest.java
│   │       │   │   ├── RegisterRequest.java
│   │       │   │   └── ApiResponse.java
│   │       │   └── exception/
│   │       │       ├── GlobalExceptionHandler.java
│   │       │       └── ResourceNotFoundException.java
│   │       └── resources/
│   │           └── application.properties
│   ├── pom.xml
│   ├── .env.example
│   └── .gitignore
├── frontend/
│   ├── index.html               (Login page)
│   ├── register.html            (Customer Registration)
│   ├── customer-dashboard.html  (Customer Dashboard)
│   ├── admin-dashboard.html     (Admin Dashboard)
│   ├── vehicles.html            (Vehicles List & Search)
│   ├── add-vehicle.html         (Register Vehicle)
│   ├── service-booking.html     (Book Service Request)
│   ├── service-requests.html    (Service Requests Management)
│   ├── service-details.html     (Detailed View & Controls)
│   ├── mechanics.html           (Mechanic Staff Roster)
│   ├── bills.html               (Billing & Invoicing)
│   ├── service-history.html     (Vehicle Maintenance History)
│   ├── css/
│   │   └── style.css            (Custom Clean Design System)
│   ├── js/
│   │   ├── api.js               (API Helpers, Auth Guards, Navbar)
│   │   ├── auth.js              (Login & Register Controller)
│   │   ├── dashboard.js         (Metric Stats Loader)
│   │   ├── vehicles.js          (Vehicles CRUD Controller)
│   │   ├── service-requests.js  (Bookings & Details Controller)
│   │   ├── mechanics.js         (Mechanic Management)
│   │   ├── bills.js             (Invoices & Calculation)
│   │   └── service-history.js   (Timeline History)
│   ├── vercel.json
│   ├── .env.example
│   └── .gitignore
├── docs/
│   ├── architecture.md
│   ├── database-design.md
│   ├── api-endpoints.md
│   └── viva-questions.md
├── README.md
├── .gitignore
└── .env.example
```

---

## 5. System Architecture Diagram

```text
+-----------------------------------------------------------+
|               CLIENT BROWSER (Desktop / Mobile)           |
+-----------------------------------------------------------+
                             |
                   HTTP GET  |  Static Files
                             v
+-----------------------------------------------------------+
|                FRONTEND HOSTING (Vercel)                  |
|          HTML5 + Custom CSS3 + Vanilla JavaScript         |
+-----------------------------------------------------------+
                             |
              JSON REST APIs | (fetch / CORS enabled)
                             v
+-----------------------------------------------------------+
|          BACKEND APPLICATION (Render / Railway)           |
|      Java 17 LTS + Spring Boot 3 + Embedded Tomcat        |
|                                                           |
|  [Controllers] -> [Services + BCrypt] -> [Repositories]   |
+-----------------------------------------------------------+
                             |
              Encrypted TLS  | Connection String (MONGODB_URI)
                             v
+-----------------------------------------------------------+
|              DATABASE TIER (MongoDB Atlas Cloud)          |
|                                                           |
| Collections:                                              |
|  1. users            2. vehicles        3. service_reqs   |
|  4. mechanics        5. bills           6. service_history|
+-----------------------------------------------------------+
```

---

## 6. MongoDB Collections Summary

1. **`users`:** Customer and administrator accounts with BCrypt-hashed credentials.
2. **`vehicles`:** Vehicle details (number plate, type, brand, model, customer reference).
3. **`service_requests`:** Maintenance bookings with status lifecycle and mechanic assignments.
4. **`mechanics`:** Technician records with specializations and live availability flags.
5. **`bills`:** Service invoices with labor, spare parts, other fees, and payment status.
6. **`service_history`:** Permanent archives of completed services and work performed.

---

## 7. How to Install & Run Locally

### Prerequisites
- **Java Development Kit (JDK 17 or 21)** installed (`java -version`).
- **Apache Maven 3.8+** installed (`mvn -version`).
- A free **MongoDB Atlas** account (or local MongoDB on port 27017).
- Modern web browser (Chrome, Edge, Firefox).
- *Node.js is NOT required.*

### Step 1: Clone or Open the Repository
```bash
git clone https://github.com/your-username/vehicle-service-management-system.git
cd vehicle-service-management-system
```

### Step 2: Configure Backend Environment Variables
In the `backend/` directory, create an `.env` file or export your MongoDB Atlas connection string:
```bash
# In backend/.env or system environment variables:
MONGODB_URI=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/vsms?retryWrites=true&w=majority
PORT=8080
FRONTEND_URL=http://localhost:3000,http://127.0.0.1:5500,http://localhost:5500,http://localhost:8080
```

### Step 3: Run the Backend
Navigate to the `backend/` folder and launch Spring Boot:
```bash
cd backend
mvn spring-boot:run
```
The server will compile and start on `http://localhost:8080`.

### Step 4: Open the Frontend
- Open `frontend/index.html` directly in your browser, **OR**
- Right-click `frontend/index.html` and click **"Open with Live Server"** in VS Code / Antigravity IDE.
- Register an account and begin using the system!

---

## 8. MongoDB Atlas Setup Guide (Step-by-Step)
1. **Create Account:** Go to [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas) and sign up for a free M0 cluster.
2. **Create Cluster:** Choose the free **Shared (M0)** tier in your nearest cloud region (e.g. Mumbai, Frankfurt, Virginia).
3. **Create Database User:** Under **Security ➔ Database Access**, create a user with username (e.g., `admin`) and a strong password (e.g., `vsms12345`). Select role: **Read and write to any database**.
4. **Network Access:** Under **Security ➔ Network Access**, click **Add IP Address** and choose **Allow Access From Anywhere** (`0.0.0.0/0`) so cloud servers can connect.
5. **Get Connection String:** Click **Connect ➔ Drivers (Java)** and copy the URI:
   ```text
   mongodb+srv://admin:vsms12345@cluster0.abcde.mongodb.net/vsms?retryWrites=true&w=majority
   ```
6. Paste this URI into your backend `MONGODB_URI` environment variable.

---

## 9. Deployment Guide

### Deploying the Backend to Render or Railway
1. Push your repository to GitHub.
2. Log into [Render.com](https://render.com) or [Railway.app](https://railway.app).
3. Create a **New Web Service** connected to your GitHub repository.
4. Set the **Root Directory** to `backend`.
5. Set the **Build Command** to: `mvn clean package -DskipTests`
6. Set the **Start Command** to: `java -jar target/vehicle-service-management-system-1.0.0.jar`
7. Add Environment Variables:
   - `MONGODB_URI` = Your MongoDB Atlas connection URI
   - `PORT` = `8080`
   - `FRONTEND_URL` = Your Vercel frontend URL
8. Copy the deployed backend URL (e.g. `https://vsms-backend.onrender.com`).

### Deploying the Frontend to Vercel
1. In `frontend/js/api.js`, update the base URL:
   ```javascript
   const API_BASE_URL = "https://vsms-backend.onrender.com";
   ```
2. Log into [Vercel.com](https://vercel.com) and import your GitHub repo.
3. In project settings, set **Root Directory** to `frontend`.
4. Deploy! Vercel will serve your static pages globally with instant load speeds.

---

## 10. Summary of REST API Endpoints

- **Auth:** `POST /api/auth/register`, `POST /api/auth/login`
- **Customers:** `GET /api/customers`, `GET /api/customers/{id}`, `PUT /api/customers/{id}`, `DELETE /api/customers/{id}`, `GET /api/customers/search`
- **Vehicles:** `POST /api/vehicles`, `GET /api/vehicles`, `GET /api/vehicles/customer/{id}`, `GET /api/vehicles/{id}`, `PUT /api/vehicles/{id}`, `DELETE /api/vehicles/{id}`, `GET /api/vehicles/search`
- **Service Requests:** `POST /api/services`, `GET /api/services`, `GET /api/services/customer/{id}`, `GET /api/services/{id}`, `PUT /api/services/{id}`, `PUT /api/services/{id}/status`, `PUT /api/services/{id}/assign`, `GET /api/services/search`
- **Mechanics:** `POST /api/mechanics`, `GET /api/mechanics`, `GET /api/mechanics/{id}`, `PUT /api/mechanics/{id}`, `DELETE /api/mechanics/{id}`
- **Bills:** `POST /api/bills`, `GET /api/bills`, `GET /api/bills/customer/{id}`, `GET /api/bills/{id}`, `PUT /api/bills/{id}`, `PUT /api/bills/{id}/payment-status`
- **Service History:** `GET /api/history/vehicle/{vehicleId}`, `GET /api/history/customer/{customerId}`, `POST /api/history`
- **Dashboard:** `GET /api/dashboard/stats`

---

## 11. Quick College Viva Q&A Cheat Sheet

1. **What is the project architecture?**  
   Three-Tier Architecture: Presentation (HTML/CSS/JS on Vercel) ➔ Application (Spring Boot REST API) ➔ Data (MongoDB Atlas).
2. **How are passwords stored?**  
   Encrypted using BCrypt one-way password hashing algorithm via Spring's `BCryptPasswordEncoder`.
3. **How does the system prevent CORS errors?**  
   Via `CorsConfig.java` in the backend which configures `allowedOrigins` and `allowedMethods`.
4. **How is service history generated?**  
   Automatically generated by the backend when a service request status is marked as "Completed".
5. **How are IDs generated?**  
   Sequential human-readable codes like `SRV-0001`, `BILL-0001`, `MEC-001` based on database count.

*(For 20+ additional questions and detailed explanations, refer to [`docs/viva-questions.md`](docs/viva-questions.md).)*

---

## License & Academic Declaration
Developed for academic assessment in **B.Sc. Computer Science (2nd Year)**. Free to adapt and learn for academic projects.
