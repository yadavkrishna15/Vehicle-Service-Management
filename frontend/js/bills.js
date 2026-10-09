/**
 * Billing Controller
 * Handles list, creation, payment status toggling, and invoice view
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = requireAuth();
    if (!user) return;
    renderNavbar();

    await loadBills();

    // Check if query params exist to open bill modal directly
    const urlParams = new URLSearchParams(window.location.search);
    const serviceId = urlParams.get("serviceId");
    if (serviceId && user.role === "ADMIN") {
        openCreateBillModal(
            serviceId,
            urlParams.get("customerId"),
            urlParams.get("vehicleId"),
            urlParams.get("amount")
        );
    }

    const billForm = document.getElementById("createBillForm");
    if (billForm) {
        billForm.addEventListener("submit", handleCreateBill);

        // Real-time calculation of total
        ["serviceCharges", "partsCharges", "otherCharges"].forEach(id => {
            const input = document.getElementById(id);
            if (input) {
                input.addEventListener("input", updateBillTotalCalculation);
            }
        });
    }
});

let cachedBills = [];

async function loadBills() {
    const user = getLoggedInUser();
    const tbody = document.querySelector("#billsTable tbody");
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;padding:2rem;">Loading bills...</td></tr>`;

    try {
        let endpoint = user.role === "ADMIN" ? "/api/bills" : `/api/bills/customer/${user.id}`;
        const res = await apiGet(endpoint);
        cachedBills = res.data || [];
        renderBillsTable(cachedBills);
    } catch (err) {
        showAlert("alertArea", "Error loading bills: " + err.message, "danger");
    }
}

function renderBillsTable(bills) {
    const user = getLoggedInUser();
    const tbody = document.querySelector("#billsTable tbody");
    if (!tbody) return;

    if (!bills || bills.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;color:var(--text-muted);padding:2rem;">No bills generated yet.</td></tr>`;
        return;
    }

    tbody.innerHTML = bills.map(b => `
        <tr>
            <td><strong>${escapeHtml(b.billId)}</strong></td>
            <td>${escapeHtml(b.serviceId || 'Direct')}</td>
            ${user.role === 'ADMIN' ? `<td>${escapeHtml(b.customerName || b.customerId || '-')}</td>` : ''}
            <td>${escapeHtml(b.vehicleNumber || '-')}</td>
            <td><strong>${formatCurrency(b.totalAmount)}</strong></td>
            <td>${formatDate(b.billDate)}</td>
            <td>
                <span class="badge ${b.paymentStatus === 'Paid' ? 'badge-paid' : 'badge-pending'}"
                      ${user.role === 'ADMIN' ? `style="cursor:pointer;" onclick="togglePaymentStatus('${b.id}', '${b.paymentStatus}')" title="Click to toggle Paid/Pending"` : ''}>
                    ${escapeHtml(b.paymentStatus)}
                </span>
            </td>
            <td>
                <div class="btn-group">
                    <button class="btn btn-sm btn-secondary" onclick="viewInvoice('${b.id}')">Invoice</button>
                    ${user.role === 'ADMIN' && b.paymentStatus !== 'Paid' ? `
                        <button class="btn btn-sm btn-success" onclick="togglePaymentStatus('${b.id}', 'Pending')">Mark Paid</button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join("");
}

function updateBillTotalCalculation() {
    const sc = parseFloat(document.getElementById("serviceCharges").value) || 0;
    const pc = parseFloat(document.getElementById("partsCharges").value) || 0;
    const oc = parseFloat(document.getElementById("otherCharges").value) || 0;
    const total = sc + pc + oc;
    const totalEl = document.getElementById("calculatedTotal");
    if (totalEl) totalEl.textContent = formatCurrency(total);
}

function openCreateBillModal(serviceId = "", customerId = "", vehicleId = "", amount = 0) {
    const modal = document.getElementById("billModal");
    if (!modal) return;

    document.getElementById("createBillForm").reset();
    if (serviceId) document.getElementById("billServiceId").value = serviceId;
    if (customerId) document.getElementById("billCustomerId").value = customerId;
    if (vehicleId) document.getElementById("billVehicleId").value = vehicleId;
    if (amount) document.getElementById("serviceCharges").value = amount;

    updateBillTotalCalculation();
    modal.classList.add("active");
}

function closeBillModal() {
    const modal = document.getElementById("billModal");
    if (modal) modal.classList.remove("active");
}

async function handleCreateBill(e) {
    e.preventDefault();
    clearAlert("modalAlertArea");

    const serviceId = document.getElementById("billServiceId").value.trim();
    const customerId = document.getElementById("billCustomerId").value.trim();
    const vehicleId = document.getElementById("billVehicleId").value.trim();
    const serviceCharges = parseFloat(document.getElementById("serviceCharges").value) || 0;
    const partsCharges = parseFloat(document.getElementById("partsCharges").value) || 0;
    const otherCharges = parseFloat(document.getElementById("otherCharges").value) || 0;
    const paymentStatus = document.getElementById("paymentStatus").value;

    const payload = {
        serviceId,
        customerId,
        vehicleId,
        serviceCharges,
        partsCharges,
        otherCharges,
        paymentStatus
    };

    const submitBtn = e.target.querySelector("button[type='submit']");
    submitBtn.disabled = true;

    try {
        const res = await apiPost("/api/bills", payload);
        if (res.success) {
            showAlert("alertArea", "Bill generated successfully! ID: " + res.data.billId, "success");
            closeBillModal();
            await loadBills();
        }
    } catch (err) {
        showAlert("modalAlertArea", err.message || "Failed to generate bill.", "danger");
    } finally {
        submitBtn.disabled = false;
    }
}

async function togglePaymentStatus(id, currentStatus) {
    const newStatus = currentStatus === "Paid" ? "Pending" : "Paid";
    try {
        await apiPut(`/api/bills/${id}/payment-status`, { status: newStatus });
        await loadBills();
        showAlert("alertArea", `Bill payment status updated to ${newStatus}.`, "success");
    } catch (err) {
        showAlert("alertArea", err.message || "Failed to update payment status.", "danger");
    }
}

function viewInvoice(id) {
    const bill = cachedBills.find(b => b.id === id);
    if (!bill) return;

    const modal = document.getElementById("invoiceModal");
    if (!modal) return;

    document.getElementById("invBillId").textContent = bill.billId;
    document.getElementById("invDate").textContent = formatDate(bill.billDate);
    document.getElementById("invCustomer").textContent = bill.customerName || bill.customerId || "N/A";
    document.getElementById("invVehicle").textContent = bill.vehicleNumber || bill.vehicleId || "N/A";
    document.getElementById("invServiceId").textContent = bill.serviceId || "Direct Billing";
    document.getElementById("invServiceCharges").textContent = formatCurrency(bill.serviceCharges);
    document.getElementById("invPartsCharges").textContent = formatCurrency(bill.partsCharges);
    document.getElementById("invOtherCharges").textContent = formatCurrency(bill.otherCharges);
    document.getElementById("invTotal").textContent = formatCurrency(bill.totalAmount);
    document.getElementById("invStatus").innerHTML = `<span class="badge ${bill.paymentStatus === 'Paid' ? 'badge-paid' : 'badge-pending'}">${escapeHtml(bill.paymentStatus)}</span>`;

    modal.classList.add("active");
}

function closeInvoiceModal() {
    const modal = document.getElementById("invoiceModal");
    if (modal) modal.classList.remove("active");
}
