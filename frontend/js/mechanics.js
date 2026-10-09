/**
 * Mechanics Controller (Admin Only)
 * Add, edit, delete mechanics and toggle availability
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = requireAuth("ADMIN");
    if (!user) return;
    renderNavbar();

    await loadMechanics();

    const addForm = document.getElementById("mechanicForm");
    if (addForm) {
        addForm.addEventListener("submit", handleSaveMechanic);
    }
});

let cachedMechanics = [];
let editingMechanicId = null;

async function loadMechanics() {
    const tbody = document.querySelector("#mechanicsTable tbody");
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;padding:2rem;">Loading mechanics...</td></tr>`;

    try {
        const res = await apiGet("/api/mechanics");
        cachedMechanics = res.data || [];
        renderMechanicsTable(cachedMechanics);
    } catch (err) {
        showAlert("alertArea", "Error loading mechanics: " + err.message, "danger");
    }
}

function renderMechanicsTable(mechanics) {
    const tbody = document.querySelector("#mechanicsTable tbody");
    if (!tbody) return;

    if (!mechanics || mechanics.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;color:var(--text-muted);padding:2rem;">No mechanics added yet. Click "Add Mechanic" to get started.</td></tr>`;
        return;
    }

    tbody.innerHTML = mechanics.map(m => `
        <tr>
            <td><strong>${escapeHtml(m.staffId)}</strong></td>
            <td>${escapeHtml(m.name)}</td>
            <td>${escapeHtml(m.phone || 'N/A')}</td>
            <td>${escapeHtml(m.specialization || 'General')}</td>
            <td>
                <span class="badge ${m.availability === 'Available' ? 'badge-available' : 'badge-busy'}" 
                      style="cursor:pointer;" 
                      onclick="toggleAvailability('${m.id}', '${m.availability}')"
                      title="Click to toggle availability">
                    ${escapeHtml(m.availability)}
                </span>
            </td>
            <td>
                <div class="btn-group">
                    <button class="btn btn-sm btn-secondary" onclick="openEditModal('${m.id}')">Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteMechanic('${m.id}')">Delete</button>
                </div>
            </td>
        </tr>
    `).join("");
}

function openAddModal() {
    editingMechanicId = null;
    document.getElementById("mechanicModalTitle").textContent = "Add New Mechanic";
    document.getElementById("mechanicForm").reset();
    document.getElementById("mechanicModal").classList.add("active");
}

function openEditModal(id) {
    const m = cachedMechanics.find(item => item.id === id);
    if (!m) return;

    editingMechanicId = id;
    document.getElementById("mechanicModalTitle").textContent = "Edit Mechanic (" + m.staffId + ")";
    document.getElementById("mName").value = m.name;
    document.getElementById("mPhone").value = m.phone || "";
    document.getElementById("mSpecialization").value = m.specialization || "General";
    document.getElementById("mAvailability").value = m.availability || "Available";

    document.getElementById("mechanicModal").classList.add("active");
}

function closeModal() {
    document.getElementById("mechanicModal").classList.remove("active");
}

async function handleSaveMechanic(e) {
    e.preventDefault();
    clearAlert("alertArea");

    const name = document.getElementById("mName").value.trim();
    const phone = document.getElementById("mPhone").value.trim();
    const specialization = document.getElementById("mSpecialization").value;
    const availability = document.getElementById("mAvailability").value;

    if (!name) {
        showAlert("modalAlertArea", "Mechanic name is required.", "danger");
        return;
    }

    const payload = { name, phone, specialization, availability };
    const submitBtn = e.target.querySelector("button[type='submit']");
    submitBtn.disabled = true;

    try {
        if (editingMechanicId) {
            await apiPut(`/api/mechanics/${editingMechanicId}`, payload);
            showAlert("alertArea", "Mechanic updated successfully!", "success");
        } else {
            await apiPost("/api/mechanics", payload);
            showAlert("alertArea", "Mechanic created successfully!", "success");
        }
        closeModal();
        await loadMechanics();
    } catch (err) {
        showAlert("modalAlertArea", err.message || "Failed to save mechanic.", "danger");
    } finally {
        submitBtn.disabled = false;
    }
}

async function toggleAvailability(id, currentStatus) {
    const newStatus = currentStatus === "Available" ? "Busy" : "Available";
    try {
        await apiPut(`/api/mechanics/${id}`, { availability: newStatus });
        await loadMechanics();
    } catch (err) {
        showAlert("alertArea", "Failed to update availability: " + err.message, "danger");
    }
}

async function deleteMechanic(id) {
    if (!confirm("Are you sure you want to delete this mechanic?")) return;
    try {
        const res = await apiDelete(`/api/mechanics/${id}`);
        if (res.success) {
            showAlert("alertArea", "Mechanic deleted successfully.", "success");
            await loadMechanics();
        }
    } catch (err) {
        showAlert("alertArea", err.message || "Failed to delete mechanic.", "danger");
    }
}
