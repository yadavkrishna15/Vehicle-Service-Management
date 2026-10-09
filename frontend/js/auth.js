/**
 * Authentication Handlers for Login and Registration
 */

document.addEventListener("DOMContentLoaded", () => {
    // Login Form Handler
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            clearAlert("alertArea");

            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value;

            if (!email || !password) {
                showAlert("alertArea", "Please enter both email and password.", "danger");
                return;
            }

            const submitBtn = loginForm.querySelector("button[type='submit']");
            submitBtn.disabled = true;
            submitBtn.textContent = "Logging in...";

            try {
                const response = await apiPost("/api/auth/login", { email, password });
                if (response.success && response.data) {
                    setLoggedInUser(response.data);
                    showAlert("alertArea", "Login successful! Redirecting...", "success");

                    setTimeout(() => {
                        if (response.data.role === "ADMIN") {
                            window.location.href = "admin-dashboard.html";
                        } else {
                            window.location.href = "customer-dashboard.html";
                        }
                    }, 600);
                } else {
                    showAlert("alertArea", response.message || "Login failed.", "danger");
                }
            } catch (err) {
                showAlert("alertArea", err.message || "Invalid credentials. Please try again.", "danger");
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = "Sign In";
            }
        });
    }

    // Register Form Handler
    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            clearAlert("alertArea");

            const name = document.getElementById("name").value.trim();
            const email = document.getElementById("email").value.trim();
            const phone = document.getElementById("phone").value.trim();
            const password = document.getElementById("password").value;
            const confirmPassword = document.getElementById("confirmPassword") ? document.getElementById("confirmPassword").value : password;
            const roleEl = document.getElementById("role");
            const role = roleEl ? roleEl.value : "CUSTOMER";

            // Validations
            if (!name || !email || !phone || !password) {
                showAlert("alertArea", "All fields are required.", "danger");
                return;
            }

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showAlert("alertArea", "Please enter a valid email address.", "danger");
                return;
            }

            if (!/^\d{10}$/.test(phone)) {
                showAlert("alertArea", "Phone number must be exactly 10 digits.", "danger");
                return;
            }

            if (password.length < 4) {
                showAlert("alertArea", "Password must be at least 4 characters long.", "danger");
                return;
            }

            if (password !== confirmPassword) {
                showAlert("alertArea", "Passwords do not match.", "danger");
                return;
            }

            const submitBtn = registerForm.querySelector("button[type='submit']");
            submitBtn.disabled = true;
            submitBtn.textContent = "Creating account...";

            try {
                const response = await apiPost("/api/auth/register", {
                    name,
                    email,
                    phone,
                    password,
                    role
                });

                if (response.success) {
                    showAlert("alertArea", "Account created successfully! Redirecting to login...", "success");
                    setTimeout(() => {
                        window.location.href = "index.html";
                    }, 1200);
                } else {
                    showAlert("alertArea", response.message || "Registration failed.", "danger");
                }
            } catch (err) {
                showAlert("alertArea", err.message || "Failed to create account.", "danger");
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = "Create Account";
            }
        });
    }
});
