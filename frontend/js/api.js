/**
 * Vehicle Service Management System (VSMS) - API & Session Helper
 * Standardized Fetch wrapper and role-based session utilities
 */

// Live Render Backend URL
const API_BASE_URL = "https://vehicle-service-management-waua.onrender.com";

const STORAGE_KEY = "vsms_user";

/**
 * Make an HTTP request and parse JSON response safely
 */
async function apiRequest(endpoint, method = "GET", data = null) {
    const url = `${API_BASE_URL}${endpoint}`;
    const options = {
        method: method,
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
    };

    if (data && (method === "POST" || method === "PUT" || method === "PATCH")) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(url, options);
        let result;
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            result = await response.json();
        } else {
            result = { success: response.ok, message: await response.text() };
        }

        if (!response.ok) {
            throw new Error(result.message || `Request failed with status ${response.status}`);
        }

        return result;
    } catch (error) {
        console.error(`API Error [${method} ${endpoint}]:`, error);
        throw error;
    }
}

// REST helper functions
function apiGet(endpoint) {
    return apiRequest(endpoint, "GET");
}

function apiPost(endpoint, data) {
    return apiRequest(endpoint, "POST", data);
}

function apiPut(endpoint, data) {
    return apiRequest(endpoint, "PUT", data);
}

function apiDelete(endpoint) {
    return apiRequest(endpoint, "DELETE");
}

/**
 * Authentication and Session Management
 */
function getLoggedInUser() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    try {
        return JSON.parse(raw);
    } catch (e) {
        localStorage.removeItem(STORAGE_KEY);
        return null;
    }
}

function setLoggedInUser(user) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
}

function logout() {
    localStorage.removeItem(STORAGE_KEY);
    window.location.href = "index.html";
}

/**
 * Protects page routes based on authentication status and user role
 * @param {string} requiredRole - "CUSTOMER", "ADMIN", or null (any logged-in user)
 */
function requireAuth(requiredRole = null) {
    const user = getLoggedInUser();
    if (!user) {
        window.location.href = "index.html";
        return null;
    }

    if (requiredRole && user.role !== requiredRole) {
        // Redirect to appropriate dashboard if role does not match
        if (user.role === "ADMIN") {
            window.location.href = "admin-dashboard.html";
        } else {
            window.location.href = "customer-dashboard.html";
        }
        return null;
    }

    return user;
}

/**
 * Renders the top navigation bar dynamically according to user role
 */
function renderNavbar() {
    const navPlaceholder = document.getElementById("navbar");
    if (!navPlaceholder) return;

    const user = getLoggedInUser();
    if (!user) {
        navPlaceholder.innerHTML = `
            <div class="nav-container">
                <a href="index.html" class="nav-brand">🔧 <span>VSMS</span></a>
                <ul class="nav-links">
                    <li><a href="index.html">Login</a></li>
                    <li><a href="register.html">Register</a></li>
                </ul>
            </div>
        `;
        return;
    }

    const currentPage = window.location.pathname.split("/").pop();
    const isAdmin = user.role === "ADMIN";

    let linksHtml = "";
    if (isAdmin) {
        linksHtml = `
            <li><a href="admin-dashboard.html" class="${currentPage === 'admin-dashboard.html' ? 'active' : ''}">Dashboard</a></li>
            <li><a href="vehicles.html" class="${currentPage === 'vehicles.html' || currentPage === 'add-vehicle.html' ? 'active' : ''}">Vehicles</a></li>
            <li><a href="service-requests.html" class="${currentPage === 'service-requests.html' || currentPage === 'service-details.html' ? 'active' : ''}">Service Requests</a></li>
            <li><a href="mechanics.html" class="${currentPage === 'mechanics.html' ? 'active' : ''}">Mechanics</a></li>
            <li><a href="bills.html" class="${currentPage === 'bills.html' ? 'active' : ''}">Bills</a></li>
            <li><a href="service-history.html" class="${currentPage === 'service-history.html' ? 'active' : ''}">Service History</a></li>
        `;
    } else {
        linksHtml = `
            <li><a href="customer-dashboard.html" class="${currentPage === 'customer-dashboard.html' ? 'active' : ''}">Dashboard</a></li>
            <li><a href="vehicles.html" class="${currentPage === 'vehicles.html' ? 'active' : ''}">My Vehicles</a></li>
            <li><a href="service-booking.html" class="${currentPage === 'service-booking.html' ? 'active' : ''}">Book Service</a></li>
            <li><a href="service-requests.html" class="${currentPage === 'service-requests.html' ? 'active' : ''}">My Bookings</a></li>
            <li><a href="bills.html" class="${currentPage === 'bills.html' ? 'active' : ''}">My Bills</a></li>
            <li><a href="service-history.html" class="${currentPage === 'service-history.html' ? 'active' : ''}">History</a></li>
        `;
    }

    navPlaceholder.innerHTML = `
        <div class="nav-container">
            <a href="${isAdmin ? 'admin-dashboard.html' : 'customer-dashboard.html'}" class="nav-brand">🔧 <span>VSMS</span></a>
            <ul class="nav-links">
                ${linksHtml}
            </ul>
            <div class="nav-user">
                <span class="user-badge">${isAdmin ? '🛡️ Admin' : '👤 Customer'}: ${escapeHtml(user.name)}</span>
                <button class="btn-logout" onclick="logout()">Logout</button>
            </div>
        </div>
    `;
}

/**
 * UI Display Helpers
 */
function showAlert(elementId, message, type = "danger") {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.innerHTML = `
        <div class="alert alert-${type}">
            <span>${escapeHtml(message)}</span>
            <button type="button" style="background:none;border:none;cursor:pointer;font-weight:bold" onclick="this.parentElement.remove()">✕</button>
        </div>
    `;
}

function clearAlert(elementId) {
    const el = document.getElementById(elementId);
    if (el) el.innerHTML = "";
}

function formatDate(dateValue) {
    if (!dateValue) return "N/A";
    const d = new Date(dateValue);
    if (isNaN(d.getTime())) return "N/A";
    return d.toLocaleDateString("en-IN", {
        year: "numeric",
        month: "short",
        day: "numeric"
    });
}

function formatCurrency(amount) {
    if (amount === null || amount === undefined) return "₹0.00";
    return "₹" + Number(amount).toFixed(2);
}

function escapeHtml(str) {
    if (!str) return "";
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function getStatusBadge(status) {
    const s = (status || "Pending").toLowerCase().replace(/\s+/g, "");
    return `<span class="badge badge-${s}">${escapeHtml(status || "Pending")}</span>`;
}
