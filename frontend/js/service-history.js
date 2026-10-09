/**
 * Service History Controller
 * Select vehicle to view detailed maintenance & repair history log
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = requireAuth();
    if (!user) return;
    renderNavbar();

    await initVehicleSelector(user);

    const vehicleSelect = document.getElementById("historyVehicleSelect");
    if (vehicleSelect) {
        vehicleSelect.addEventListener("change", () => {
            const selectedId = vehicleSelect.value;
            if (selectedId) {
                loadHistoryForVehicle(selectedId);
            } else {
                renderEmptyHistory();
            }
        });
    }
});

async function initVehicleSelector(user) {
    const select = document.getElementById("historyVehicleSelect");
    if (!select) return;

    try {
        let endpoint = user.role === "ADMIN" ? "/api/vehicles" : `/api/vehicles/customer/${user.id}`;
        const res = await apiGet(endpoint);
        const vehicles = res.data || [];

        if (vehicles.length === 0) {
            select.innerHTML = `<option value="">-- No vehicles found --</option>`;
            renderEmptyHistory();
            return;
        }

        const urlParams = new URLSearchParams(window.location.search);
        const urlVehicleId = urlParams.get("vehicleId");

        select.innerHTML = `<option value="">-- Choose a Vehicle to View History --</option>` +
            vehicles.map(v => `
                <option value="${v.id}" ${v.id === urlVehicleId ? 'selected' : ''}>
                    ${escapeHtml(v.vehicleNumber)} - ${escapeHtml(v.brand || '')} ${escapeHtml(v.model || '')} (${escapeHtml(v.vehicleType || 'Vehicle')})
                </option>
            `).join("");

        if (urlVehicleId) {
            await loadHistoryForVehicle(urlVehicleId);
        } else if (vehicles.length > 0) {
            // Auto select first vehicle
            select.value = vehicles[0].id;
            await loadHistoryForVehicle(vehicles[0].id);
        }
    } catch (err) {
        showAlert("alertArea", "Error loading vehicle options: " + err.message, "danger");
    }
}

async function loadHistoryForVehicle(vehicleId) {
    const container = document.getElementById("historyListContainer");
    if (!container) return;

    container.innerHTML = `<p style="text-align:center;padding:2rem;color:var(--text-muted);">Fetching service history...</p>`;

    try {
        const res = await apiGet(`/api/history/vehicle/${vehicleId}`);
        const historyList = res.data || [];
        renderHistoryList(historyList);
    } catch (err) {
        showAlert("alertArea", "Failed to load history: " + err.message, "danger");
    }
}

function renderHistoryList(list) {
    const container = document.getElementById("historyListContainer");
    if (!container) return;

    if (!list || list.length === 0) {
        container.innerHTML = `
            <div class="card" style="text-align:center;padding:3rem 1rem;color:var(--text-muted);">
                <p style="font-size:1.1rem;font-weight:600;margin-bottom:0.5rem;">No Service History Found</p>
                <p>This vehicle does not have any completed service records yet.</p>
            </div>
        `;
        return;
    }

    // Sort newest first
    const sorted = [...list].sort((a, b) => new Date(b.serviceDate) - new Date(a.serviceDate));

    container.innerHTML = sorted.map(item => `
        <div class="card" style="margin-bottom:1.25rem;border-left:4px solid var(--primary);">
            <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:0.5rem;margin-bottom:0.75rem;">
                <div>
                    <h3 style="font-size:1.1rem;color:var(--dark);margin-bottom:0.25rem;">
                        ${escapeHtml(item.serviceType || 'General Service')} 
                        <span style="font-size:0.85rem;color:var(--text-muted);font-weight:normal;">(${escapeHtml(item.serviceId)})</span>
                    </h3>
                    <p style="font-size:0.85rem;color:var(--text-muted);">
                        📅 Serviced on <strong>${formatDate(item.serviceDate)}</strong>
                    </p>
                </div>
                <div style="text-align:right;">
                    <span class="badge badge-completed">${escapeHtml(item.status || 'Completed')}</span>
                    <p style="font-size:1.15rem;font-weight:700;color:var(--primary);margin-top:0.35rem;">
                        ${formatCurrency(item.cost)}
                    </p>
                </div>
            </div>
            
            <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(220px, 1fr));gap:1rem;background:#f8fafc;padding:0.85rem;border-radius:var(--radius-sm);margin-top:0.5rem;">
                <div>
                    <strong style="font-size:0.8rem;text-transform:uppercase;color:var(--text-muted);">Problem Reported:</strong>
                    <p style="font-size:0.9rem;margin-top:0.2rem;">${escapeHtml(item.problem || 'Routine maintenance')}</p>
                </div>
                <div>
                    <strong style="font-size:0.8rem;text-transform:uppercase;color:var(--text-muted);">Work Performed:</strong>
                    <p style="font-size:0.9rem;margin-top:0.2rem;">${escapeHtml(item.workPerformed || 'Standard service completed')}</p>
                </div>
                <div>
                    <strong style="font-size:0.8rem;text-transform:uppercase;color:var(--text-muted);">Serviced By:</strong>
                    <p style="font-size:0.9rem;margin-top:0.2rem;">👨‍🔧 ${escapeHtml(item.mechanicName || 'Service Specialist')}</p>
                </div>
            </div>
        </div>
    `).join("");
}

function renderEmptyHistory() {
    const container = document.getElementById("historyListContainer");
    if (container) {
        container.innerHTML = `
            <div class="card" style="text-align:center;padding:3rem 1rem;color:var(--text-muted);">
                <p>Please select a vehicle above to see its complete service history.</p>
            </div>
        `;
    }
}
