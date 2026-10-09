/**
 * Dashboard Controller for Admin & Customer Dashboards
 */

document.addEventListener("DOMContentLoaded", async () => {
    // Determine which dashboard is open
    const isAdminDashboard = document.getElementById("adminDashboardSection") !== null;
    const isCustomerDashboard = document.getElementById("customerDashboardSection") !== null;

    if (isAdminDashboard) {
        const user = requireAuth("ADMIN");
        if (!user) return;
        renderNavbar();
        await loadAdminDashboardData();
    } else if (isCustomerDashboard) {
        const user = requireAuth("CUSTOMER");
        if (!user) return;
        renderNavbar();
        await loadCustomerDashboardData(user);
    }
});

async function loadAdminDashboardData() {
    try {
        const statsRes = await apiGet("/api/dashboard/stats");
        if (statsRes.success && statsRes.data) {
            const s = statsRes.data;
            document.getElementById("statCustomers").textContent = s.totalCustomers || 0;
            document.getElementById("statVehicles").textContent = s.totalVehicles || 0;
            document.getElementById("statRequests").textContent = s.totalServiceRequests || 0;
            document.getElementById("statPending").textContent = s.pendingServices || 0;
            document.getElementById("statInProgress").textContent = s.inProgressServices || 0;
            document.getElementById("statCompleted").textContent = s.completedServices || 0;
            document.getElementById("statBills").textContent = s.totalBills || 0;
        }

        // Load recent 5 service requests
        const reqRes = await apiGet("/api/services");
        const tbody = document.getElementById("recentRequestsTable");
        if (tbody && reqRes.success) {
            const list = (reqRes.data || []).slice(-5).reverse();
            if (list.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;color:var(--text-muted);padding:2rem;">No service requests yet.</td></tr>`;
            } else {
                tbody.innerHTML = list.map(item => `
                    <tr>
                        <td><strong>${escapeHtml(item.serviceId)}</strong></td>
                        <td>${escapeHtml(item.customerName || item.customerId)}</td>
                        <td>${escapeHtml(item.vehicleNumber || 'N/A')}</td>
                        <td>${escapeHtml(item.serviceType)}</td>
                        <td>${getStatusBadge(item.status)}</td>
                        <td><a href="service-details.html?id=${item.id}" class="btn btn-sm btn-secondary">View / Manage</a></td>
                    </tr>
                `).join("");
            }
        }
    } catch (err) {
        showAlert("alertArea", "Failed to load dashboard data: " + err.message, "danger");
    }
}

async function loadCustomerDashboardData(user) {
    try {
        // Welcome message
        const welcomeEl = document.getElementById("welcomeName");
        if (welcomeEl) welcomeEl.textContent = user.name;

        // Stats
        const statsRes = await apiGet(`/api/dashboard/stats?customerId=${user.id}`);
        if (statsRes.success && statsRes.data) {
            const s = statsRes.data;
            document.getElementById("statCustomerVehicles").textContent = s.customerVehicles || 0;
            document.getElementById("statActiveServices").textContent = s.activeServices || 0;
            document.getElementById("statCompletedServices").textContent = s.completedServices || 0;
            document.getElementById("statCustomerBills").textContent = s.totalBills || 0;
        }

        // Recent requests for this customer
        const reqRes = await apiGet(`/api/services/customer/${user.id}`);
        const tbody = document.getElementById("customerRequestsTable");
        if (tbody && reqRes.success) {
            const list = (reqRes.data || []).slice(-5).reverse();
            if (list.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;color:var(--text-muted);padding:2rem;">You have not booked any services yet.</td></tr>`;
            } else {
                tbody.innerHTML = list.map(item => `
                    <tr>
                        <td><strong>${escapeHtml(item.serviceId)}</strong></td>
                        <td>${escapeHtml(item.vehicleNumber || 'N/A')}</td>
                        <td>${escapeHtml(item.serviceType)}</td>
                        <td>${formatDate(item.preferredDate)}</td>
                        <td>${getStatusBadge(item.status)}</td>
                        <td><a href="service-details.html?id=${item.id}" class="btn btn-sm btn-secondary">Details</a></td>
                    </tr>
                `).join("");
            }
        }
    } catch (err) {
        showAlert("alertArea", "Failed to load customer stats: " + err.message, "danger");
    }
}
