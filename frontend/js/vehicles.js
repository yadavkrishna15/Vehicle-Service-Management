/**
 * Vehicles Controller: List, Search, Add, Delete
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = requireAuth();
    if (!user) return;
    renderNavbar();

    // Check if on vehicles list page
    const vehiclesTable = document.getElementById("vehiclesTable");
    if (vehiclesTable) {
        await loadVehicles();

        const searchInput = document.getElementById("vehicleSearchInput");
        if (searchInput) {
            searchInput.addEventListener("input", debounce(async () => {
                const query = searchInput.value.trim();
                await searchVehicles(query);
            }, 300));
        }
    }

    // Check if on add vehicle page
    const addVehicleForm = document.getElementById("addVehicleForm");
    if (addVehicleForm) {
        addVehicleForm.addEventListener("submit", handleAddVehicle);
    }
});

let cachedVehicles = [];

async function loadVehicles() {
    const user = getLoggedInUser();
    const tbody = document.querySelector("#vehiclesTable tbody");
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;padding:2rem;">Loading vehicles...</td></tr>`;

    try {
        let endpoint = user.role === "ADMIN" ? "/api/vehicles" : `/api/vehicles/customer/${user.id}`;
        const response = await apiGet(endpoint);
        cachedVehicles = response.data || [];
        renderVehiclesTable(cachedVehicles);
    } catch (err) {
        showAlert("alertArea", "Error loading vehicles: " + err.message, "danger");
    }
}

async function searchVehicles(query) {
    const user = getLoggedInUser();
    if (!query) {
        renderVehiclesTable(cachedVehicles);
        return;
    }

    try {
        const response = await apiGet(`/api/vehicles/search?vehicleNumber=${encodeURIComponent(query)}`);
        let results = response.data || [];
        // If customer, only show results belonging to them
        if (user.role !== "ADMIN") {
            results = results.filter(v => v.customerId === user.id);
        }
        renderVehiclesTable(results);
    } catch (err) {
        console.error("Search error", err);
    }
}

function renderVehiclesTable(vehicles) {
    const user = getLoggedInUser();
    const tbody = document.querySelector("#vehiclesTable tbody");
    if (!tbody) return;

    if (!vehicles || vehicles.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:var(--text-muted);padding:2rem;">No vehicles found. Click "Add Vehicle" to register one.</td></tr>`;
        return;
    }

    tbody.innerHTML = vehicles.map(v => `
        <tr>
            <td><strong>${escapeHtml(v.vehicleNumber)}</strong></td>
            <td>${escapeHtml(v.vehicleType || 'Car')}</td>
            <td>${escapeHtml(v.brand || '-')} ${escapeHtml(v.model || '')}</td>
            <td>${escapeHtml(v.manufacturingYear || '-')}</td>
            <td>${escapeHtml(v.fuelType || '-')}</td>
            ${user.role === 'ADMIN' ? `<td>${escapeHtml(v.customerName || v.customerId || '-')}</td>` : ''}
            <td>
                <div class="btn-group">
                    <a href="service-booking.html?vehicleId=${v.id}" class="btn btn-sm btn-primary">Book Service</a>
                    <a href="service-history.html?vehicleId=${v.id}" class="btn btn-sm btn-secondary">History</a>
                    ${user.role === 'ADMIN' ? `
                        <button class="btn btn-sm btn-danger" onclick="deleteVehicle('${v.id}')">Delete</button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join("");
}

async function handleAddVehicle(e) {
    e.preventDefault();
    clearAlert("alertArea");

    const user = getLoggedInUser();
    const vehicleNumber = document.getElementById("vehicleNumber").value.trim().toUpperCase();
    const vehicleType = document.getElementById("vehicleType").value;
    const brand = document.getElementById("brand").value.trim();
    const model = document.getElementById("model").value.trim();
    const manufacturingYearVal = document.getElementById("manufacturingYear").value;
    const fuelType = document.getElementById("fuelType").value;

    if (!vehicleNumber) {
        showAlert("alertArea", "Vehicle registration number is required.", "danger");
        return;
    }

    const payload = {
        vehicleNumber,
        vehicleType,
        brand,
        model,
        manufacturingYear: manufacturingYearVal ? parseInt(manufacturingYearVal) : null,
        fuelType,
        customerId: user.id,
        customerName: user.name,
        customerContact: user.phone
    };

    const submitBtn = e.target.querySelector("button[type='submit']");
    submitBtn.disabled = true;
    submitBtn.textContent = "Saving...";

    try {
        const response = await apiPost("/api/vehicles", payload);
        if (response.success) {
            showAlert("alertArea", "Vehicle registered successfully! Redirecting...", "success");
            setTimeout(() => {
                window.location.href = "vehicles.html";
            }, 800);
        } else {
            showAlert("alertArea", response.message || "Failed to add vehicle.", "danger");
        }
    } catch (err) {
        showAlert("alertArea", err.message || "Failed to add vehicle.", "danger");
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = "Register Vehicle";
    }
}

async function deleteVehicle(id) {
    if (!confirm("Are you sure you want to delete this vehicle?")) return;

    try {
        const res = await apiDelete(`/api/vehicles/${id}`);
        if (res.success) {
            showAlert("alertArea", "Vehicle deleted successfully.", "success");
            await loadVehicles();
        } else {
            showAlert("alertArea", res.message || "Could not delete vehicle.", "danger");
        }
    } catch (err) {
        showAlert("alertArea", err.message || "Failed to delete vehicle.", "danger");
    }
}

function debounce(func, delay) {
    let timeout;
    return (...args) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => func(...args), delay);
    };
}
