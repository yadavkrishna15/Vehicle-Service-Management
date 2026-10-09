# REST API Specification: Vehicle Service Management System (VSMS)

Base URL (Local): `http://localhost:8080`  
Standard Response Format:
```json
{
  "success": true,
  "message": "Operation description",
  "data": { ... }
}
```

---

## 1. Authentication Endpoints (`/api/auth`)

### 1.1 Register User
- **Method:** `POST`
- **URL:** `/api/auth/register`
- **Request Body:**
```json
{
  "name": "Arjun Kumar",
  "email": "arjun@example.com",
  "password": "secretPassword123",
  "phone": "9876543210",
  "role": "CUSTOMER"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful! You can now log in.",
  "data": {
    "id": "6612e4f0a1b2c3d4e5f60001",
    "name": "Arjun Kumar",
    "email": "arjun@example.com",
    "phone": "9876543210",
    "role": "CUSTOMER",
    "createdAt": "2026-10-09T16:00:00.000Z"
  }
}
```

### 1.2 User Login
- **Method:** `POST`
- **URL:** `/api/auth/login`
- **Request Body:**
```json
{
  "email": "arjun@example.com",
  "password": "secretPassword123"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful!",
  "data": {
    "id": "6612e4f0a1b2c3d4e5f60001",
    "name": "Arjun Kumar",
    "email": "arjun@example.com",
    "phone": "9876543210",
    "role": "CUSTOMER",
    "createdAt": "2026-10-09T16:00:00.000Z"
  }
}
```

---

## 2. Customer Management Endpoints (`/api/customers`)

| Method | Endpoint | Description | Query / Params |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/customers` | List all registered customers | None |
| `GET` | `/api/customers/{id}` | Get customer by ID | `id` path variable |
| `PUT` | `/api/customers/{id}` | Update customer profile | JSON `{ "name": "...", "phone": "..." }` |
| `DELETE` | `/api/customers/{id}` | Remove customer record | `id` path variable |
| `GET` | `/api/customers/search` | Search customers by name | `?name=Arjun` |

---

## 3. Vehicle Endpoints (`/api/vehicles`)

### 3.1 Register Vehicle
- **Method:** `POST`
- **URL:** `/api/vehicles`
- **Request Body:**
```json
{
  "vehicleNumber": "MH12AB1234",
  "vehicleType": "Car",
  "brand": "Honda",
  "model": "City",
  "manufacturingYear": 2022,
  "fuelType": "Petrol",
  "customerId": "6612e4f0a1b2c3d4e5f60001"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "Vehicle registered successfully",
  "data": {
    "id": "6612e510a1b2c3d4e5f60002",
    "vehicleNumber": "MH12AB1234",
    "vehicleType": "Car",
    "brand": "Honda",
    "model": "City",
    "manufacturingYear": 2022,
    "fuelType": "Petrol",
    "customerId": "6612e4f0a1b2c3d4e5f60001",
    "customerName": "Arjun Kumar",
    "customerContact": "9876543210"
  }
}
```

### 3.2 Vehicle Query Endpoints
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/vehicles` | List all registered vehicles (Admin) |
| `GET` | `/api/vehicles/customer/{customerId}` | List vehicles belonging to one customer |
| `GET` | `/api/vehicles/{id}` | Retrieve vehicle details |
| `PUT` | `/api/vehicles/{id}` | Update vehicle specs |
| `DELETE` | `/api/vehicles/{id}` | Remove vehicle |
| `GET` | `/api/vehicles/search?vehicleNumber=...` | Search by registration plate |

---

## 4. Service Request Endpoints (`/api/services`)

### 4.1 Book Service Request
- **Method:** `POST`
- **URL:** `/api/services`
- **Request Body:**
```json
{
  "customerId": "6612e4f0a1b2c3d4e5f60001",
  "vehicleId": "6612e510a1b2c3d4e5f60002",
  "serviceType": "Oil Change",
  "problemDescription": "Engine oil replacement and filter cleaning",
  "preferredDate": "2026-10-15"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "Service request submitted successfully! Tracking ID: SRV-0001",
  "data": {
    "id": "6612e540a1b2c3d4e5f60003",
    "serviceId": "SRV-0001",
    "customerId": "6612e4f0a1b2c3d4e5f60001",
    "customerName": "Arjun Kumar",
    "vehicleId": "6612e510a1b2c3d4e5f60002",
    "vehicleNumber": "MH12AB1234",
    "serviceType": "Oil Change",
    "status": "Requested",
    "estimatedCost": 0.0,
    "finalCost": 0.0
  }
}
```

### 4.2 Update Status / Assign Mechanic
| Method | Endpoint | Description | Payload |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/services` | List all service requests | None |
| `GET` | `/api/services/customer/{customerId}` | Customer's bookings | None |
| `GET` | `/api/services/{id}` | Get single request | None |
| `PUT` | `/api/services/{id}` | Update full request details | Request JSON |
| `PUT` | `/api/services/{id}/status` | Advance status | `{"status": "Completed"}` |
| `PUT` | `/api/services/{id}/assign` | Assign mechanic | `{"mechanicId": "..."}` |
| `GET` | `/api/services/search` | Filter status / search ID | `?status=In%20Service` or `?id=SRV-0001` |

---

## 5. Mechanic Staff Endpoints (`/api/mechanics`)

| Method | Endpoint | Description | Payload |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/mechanics` | Add new mechanic | `{"name":"Ramesh","specialization":"Engine"}` |
| `GET` | `/api/mechanics` | List all mechanics | None |
| `GET` | `/api/mechanics/{id}` | Get mechanic details | None |
| `PUT` | `/api/mechanics/{id}` | Update mechanic info/status | `{"availability":"Busy"}` |
| `DELETE` | `/api/mechanics/{id}` | Delete mechanic record | None |

---

## 6. Billing Endpoints (`/api/bills`)

### 6.1 Generate Bill
- **Method:** `POST`
- **URL:** `/api/bills`
- **Request Body:**
```json
{
  "serviceId": "SRV-0001",
  "customerId": "6612e4f0a1b2c3d4e5f60001",
  "vehicleId": "6612e510a1b2c3d4e5f60002",
  "serviceCharges": 800.0,
  "partsCharges": 1200.0,
  "otherCharges": 100.0,
  "paymentStatus": "Pending"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "Bill generated successfully! Invoice ID: BILL-0001",
  "data": {
    "id": "6612e580a1b2c3d4e5f60004",
    "billId": "BILL-0001",
    "serviceId": "SRV-0001",
    "totalAmount": 2100.0,
    "paymentStatus": "Pending"
  }
}
```

### 6.2 Billing Query & Status Endpoints
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/bills` | List all bills (Admin) |
| `GET` | `/api/bills/customer/{customerId}` | List customer invoices |
| `GET` | `/api/bills/{id}` | Get single bill by ID |
| `PUT` | `/api/bills/{id}` | Update bill amounts |
| `PUT` | `/api/bills/{id}/payment-status` | Toggle payment status (`Pending` / `Paid`) |

---

## 7. Service History Endpoints (`/api/history`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/history/vehicle/{vehicleId}` | Get chronological service log for vehicle |
| `GET` | `/api/history/customer/{customerId}` | Get all history records for customer |
| `POST` | `/api/history` | Record manual service history entry |

---

## 8. Dashboard Statistics Endpoint (`/api/dashboard`)

### 8.1 Get Statistics
- **Method:** `GET`
- **URL:** `/api/dashboard/stats`
- **Query Parameter (Optional):** `?customerId=...`
- **Admin Response (200 OK):**
```json
{
  "success": true,
  "message": "Dashboard statistics retrieved",
  "data": {
    "totalCustomers": 12,
    "totalVehicles": 24,
    "totalServiceRequests": 30,
    "pendingServices": 8,
    "inProgressServices": 4,
    "completedServices": 18,
    "totalBills": 18
  }
}
```
