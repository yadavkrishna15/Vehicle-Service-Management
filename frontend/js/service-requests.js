/**
 * Service Requests Controller
 * Handles booking, listing, filtering, updating status, and details view
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = requireAuth();
    if (!user) return;
    renderNavbar();

    // 1. Service Booking Page
    const bookingForm = document.getElementById("serviceBookingForm");
    if (bookingForm) {
        await initBookingPage(user);
        bookingForm.addEventListener("submit", handleBookingSubmit);
    }

    // 2. Service Requests List Page
    const requestsTable = document.getElementById("serviceRequestsTable");
    if (requestsTable) {
        await loadServiceRequests();

        const statusFilter = document.getElementById("statusFilter");
        if (statusFilter) {
            statusFilter.addEventListener("change", () => filterRequests(statusFilter.value));
        }

        const searchInput = document.getElementById("searchRequestId");
        if (searchInput) {
            searchInput.addEventListener("input", () => searchByReqId(searchInput.value.trim()));
        }
    }

    // 3. Service Details Page
    const detailsContainer = document.getElementById("serviceDetailsContainer");
    if (detailsContainer) {
        await loadServiceDetails(user);
    }
});

let allRequests = [];

/* ==========================================================================
   Booking Logic
   ========================================================================== */
async function initBookingPage(user) {
    const vehicleSelect = document.getElementById("vehicleSelect");
    if (!vehicleSelect) return;

    try {
        const res = await apiGet(`/api/vehicles/customer/${user.id}`);
        const vehicles = res.data || [];

        if (vehicles.length === 0) {
            showAlert("alertArea", "You have not registered any vehicles yet. Please add a vehicle first.", "info");
            vehicleSelect.innerHTML = `<option value="">-- No vehicles found --</option>`;
            const submitBtn = document.querySelector("#serviceBookingForm button[type='submit']");
            if (submitBtn) submitBtn.disabled = true;
            return;
        }

        const urlParams = new URLSearchParams(window.location.search);
        const preselectedVehicleId = urlParams.get("vehicleId");

        vehicleSelect.innerHTML = `<option value="">-- Select a Vehicle --</option>` +
            vehicles.map(v => `
                <option value="${v.id}" ${v.id === preselectedVehicleId ? 'selected' : ''}>
                    ${escapeHtml(v.vehicleNumber)} - ${escapeHtml(v.brand || '')} ${escapeHtml(v.model || '')} (${escapeHtml(v.vehicleType || 'Vehicle')})
                </option>
            `).join("");

        // Set min date to today for date picker
        const dateInput = document.getElementById("preferredDate");
        if (dateInput) {
            const today = new Date().toISOString().split("T")[0];
            dateInput.min = today;
            dateInput.value = today;
        }
    } catch (err) {
        showAlert("alertArea", "Failed to load vehicles: " + err.message, "danger");
    }
}

async function handleBookingSubmit(e) {
    e.preventDefault();
    clearAlert("alertArea");

    const user = getLoggedInUser();
    const vehicleId = document.getElementById("vehicleSelect").value;
    const serviceType = document.getElementById("serviceType").value;
    const preferredDate = document.getElementById("preferredDate").value;
    const problemDescription = document.getElementById("problemDescription").value.trim();

    if (!vehicleId) {
        showAlert("alertArea", "Please select a vehicle.", "danger");
        return;
    }
    if (!serviceType) {
        showAlert("alertArea", "Please choose a service type.", "danger");
        return;
    }
    if (!preferredDate) {
        showAlert("alertArea", "Please choose a preferred service date.", "danger");
        return;
    }

    const payload = {
        customerId: user.id,
        vehicleId: vehicleId,
        serviceType: serviceType,
        preferredDate: preferredDate,
        problemDescription: problemDescription || "Standard " + serviceType
    };

    const submitBtn = e.target.querySelector("button[type='submit']");
    submitBtn.disabled = true;
    submitBtn.textContent = "Booking Service...";

    try {
        const response = await apiPost("/api/services", payload);
        if (response.success && response.data) {
            showAlert("alertArea", "Service booked successfully! Tracking ID: " + response.data.serviceId, "success");
            setTimeout(() => {
                window.location.href = "service-requests.html";
            }, 1200);
        } else {
            showAlert("alertArea", response.message || "Booking failed.", "danger");
        }
    } catch (err) {
        showAlert("alertArea", err.message || "Failed to book service.", "danger");
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = "Book Service Request";
    }
}

/* ==========================================================================
   List & Filter Requests
   ========================================================================== */
async function loadServiceRequests() {
    const user = getLoggedInUser();
    const tbody = document.querySelector("#serviceRequestsTable tbody");
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;padding:2rem;">Loading service requests...</td></tr>`;

    try {
        let endpoint = user.role === "ADMIN" ? "/api/services" : `/api/services/customer/${user.id}`;
        const res = await apiGet(endpoint);
        allRequests = res.data || [];
        renderRequestsTable(allRequests);
    } catch (err) {
        showAlert("alertArea", "Failed to load requests: " + err.message, "danger");
    }
}

function filterRequests(status) {
    if (!status || status === "ALL") {
        renderRequestsTable(allRequests);
    } else {
        const filtered = allRequests.filter(r => (r.status || "").toLowerCase() === status.toLowerCase());
        renderRequestsTable(filtered);
    }
}

function searchByReqId(query) {
    if (!query) {
        renderRequestsTable(allRequests);
        return;
    }
    const q = query.toLowerCase();
    const filtered = allRequests.filter(r =>
        (r.serviceId && r.serviceId.toLowerCase().includes(q)) ||
        (r.vehicleNumber && r.vehicleNumber.toLowerCase().includes(q))
    );
    renderRequestsTable(filtered);
}

function renderRequestsTable(list) {
    const user = getLoggedInUser();
    const tbody = document.querySelector("#serviceRequestsTable tbody");
    if (!tbody) return;

    if (!list || list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:var(--text-muted);padding:2rem;">No service requests found.</td></tr>`;
        return;
    }

    tbody.innerHTML = list.map(req => `
        <tr>
            <td><strong>${escapeHtml(req.serviceId)}</strong></td>
            ${user.role === 'ADMIN' ? `<td>${escapeHtml(req.customerName || req.customerId || 'Customer')}</td>` : ''}
            <td>${escapeHtml(req.vehicleNumber || 'Vehicle')}</td>
            <td>${escapeHtml(req.serviceType)}</td>
            <td>${formatDate(req.preferredDate)}</td>
            <td>${getStatusBadge(req.status)}</td>
            <td>
                <div class="btn-group">
                    <a href="service-details.html?id=${req.id}" class="btn btn-sm btn-secondary">Details</a>
                    ${user.role === 'ADMIN' && req.status !== 'Completed' && req.status !== 'Cancelled' ? `
                        <button class="btn btn-sm btn-success" onclick="quickComplete('${req.id}')" title="Mark as Completed">✓ Done</button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join("");
}

async function quickComplete(id) {
    if (!confirm("Are you sure you want to mark this service as Completed? This will automatically record it in Service History.")) return;
    try {
        const res = await apiPut(`/api/services/${id}/status`, { status: "Completed" });
        if (res.success) {
            showAlert("alertArea", "Service marked as Completed! History entry generated.", "success");
            await loadServiceRequests();
        }
    } catch (err) {
        showAlert("alertArea", err.message || "Failed to update status", "danger");
    }
}

/* ==========================================================================
   Service Details Page
   ========================================================================== */
async function loadServiceDetails(user) {
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get("id");

    if (!id) {
        showAlert("alertArea", "Invalid service request ID.", "danger");
        return;
    }

    try {
        const res = await apiGet(`/api/services/${id}`);
        if (!res.success || !res.data) {
            showAlert("alertArea", "Service request not found.", "danger");
            return;
        }

        const req = res.data;
        document.getElementById("detailServiceId").textContent = req.serviceId;
        document.getElementById("detailCustomerName").textContent = req.customerName || req.customerId || "N/A";
        document.getElementById("detailVehicleNumber").textContent = req.vehicleNumber || "N/A";
        document.getElementById("detailServiceType").textContent = req.serviceType;
        document.getElementById("detailPreferredDate").textContent = formatDate(req.preferredDate);
        document.getElementById("detailStatus").innerHTML = getStatusBadge(req.status);
        document.getElementById("detailProblem").textContent = req.problemDescription || "None reported";
        document.getElementById("detailMechanic").textContent = req.assignedMechanicName || "Not assigned yet";
        document.getElementById("detailEstCost").textContent = formatCurrency(req.estimatedCost);
        document.getElementById("detailFinalCost").textContent = formatCurrency(req.finalCost);

        const adminSection = document.getElementById("adminControlsSection");
        if (adminSection) {
            if (user.role === "ADMIN") {
                adminSection.style.display = "block";
                await initAdminControls(req);
            } else {
                adminSection.style.display = "none";
            }
        }
    } catch (err) {
        showAlert("alertArea", "Error loading details: " + err.message, "danger");
    }
}

async function initAdminControls(req) {
    // Populate Status Select
    const statusSelect = document.getElementById("updateStatusSelect");
    if (statusSelect) {
        statusSelect.value = req.status;
    }

    // Populate Mechanics Select
    const mechanicSelect = document.getElementById("updateMechanicSelect");
    if (mechanicSelect) {
        try {
            const mRes = await apiGet("/api/mechanics");
            const mechanics = mRes.data || [];
            mechanicSelect.innerHTML = `<option value="">-- Unassigned --</option>` +
                mechanics.map(m => `
                    <option value="${m.id}" ${m.id === req.assignedMechanicId ? 'selected' : ''}>
                        ${escapeHtml(m.name)} (${escapeHtml(m.specialization || 'General')} - ${escapeHtml(m.availability)})
                    </option>
                `).join("");
        } catch (e) {
            console.error("Mechanics load error", e);
        }
    }

    // Costs
    const estInput = document.getElementById("updateEstCost");
    if (estInput) estInput.value = req.estimatedCost || 0;

    const finalInput = document.getElementById("updateFinalCost");
    if (finalInput) finalInput.value = req.finalCost || 0;

    // Handle Save Button
    const saveBtn = document.getElementById("saveDetailsBtn");
    if (saveBtn) {
        saveBtn.onclick = async () => {
            clearAlert("alertArea");
            const newStatus = statusSelect ? statusSelect.value : req.status;
            const newMechanicId = mechanicSelect ? mechanicSelect.value : req.assignedMechanicId;
            const estCost = estInput ? parseFloat(estInput.value) || 0 : req.estimatedCost;
            const finalCost = finalInput ? parseFloat(finalInput.value) || 0 : req.finalCost;

            saveBtn.disabled = true;
            saveBtn.textContent = "Saving changes...";

            try {
                // Update basic fields & costs
                await apiPut(`/api/services/${req.id}`, {
                    serviceType: req.serviceType,
                    problemDescription: req.problemDescription,
                    preferredDate: req.preferredDate,
                    estimatedCost: estCost,
                    finalCost: finalCost
                });

                // Assign mechanic if changed
                if (newMechanicId !== req.assignedMechanicId) {
                    await apiPut(`/api/services/${req.id}/assign`, { mechanicId: newMechanicId });
                }

                // Update status if changed
                if (newStatus !== req.status) {
                    await apiPut(`/api/services/${req.id}/status`, { status: newStatus });
                }

                showAlert("alertArea", "Service request updated successfully!", "success");
                setTimeout(() => window.location.reload(), 800);
            } catch (err) {
                showAlert("alertArea", "Failed to update: " + err.message, "danger");
            } finally {
                saveBtn.disabled = false;
                saveBtn.textContent = "Save Changes";
            }
        };
    }

    // Quick Bill Link
    const createBillBtn = document.getElementById("createBillBtn");
    if (createBillBtn) {
        createBillBtn.href = `bills.html?serviceId=${encodeURIComponent(req.serviceId)}&customerId=${encodeURIComponent(req.customerId)}&vehicleId=${encodeURIComponent(req.vehicleId)}&amount=${req.finalCost || req.estimatedCost || 0}`;
    }
}
