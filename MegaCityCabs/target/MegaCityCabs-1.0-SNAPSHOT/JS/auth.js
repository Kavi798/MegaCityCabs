document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");
    const logoutButton = document.getElementById("logoutButton");
    const loginLink = document.getElementById("loginLink");
    const registerLink = document.getElementById("registerLink");

    const API_BASE_URL = "http://localhost:8080/ServiceBackend/api/users"; // Backend API Base URL

    // Check if user is logged in
    const loggedInUser = JSON.parse(sessionStorage.getItem("loggedInUser"));

    // 🚀 If user is NOT logged in & they are on `index.html`, redirect to login
    if (!loggedInUser && window.location.pathname.includes("index.html")) {
        window.location.href = "login.html";
    }

    if (loggedInUser) {
        console.log(`Welcome back, ${loggedInUser.name}`);

        // Show Logout Button & Hide Login/Register Links
        if (logoutButton) logoutButton.style.display = "inline-block";
        if (loginLink) loginLink.style.display = "none";
        if (registerLink) registerLink.style.display = "none";
    } else {
        // Hide Logout Button if user is NOT logged in
        if (logoutButton) logoutButton.style.display = "none";
        if (loginLink) loginLink.style.display = "inline-block";
        if (registerLink) registerLink.style.display = "inline-block";
    }

    // 🔹 Handle User Login (Connect to Backend)
    if (loginForm) {
        loginForm.addEventListener("submit", async function (event) {
            event.preventDefault();

            const email = document.getElementById("email").value;
            const password = document.getElementById("password").value;

            const loginData = { email, password };

            try {
                const response = await fetch(`${API_BASE_URL}/login`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(loginData)
                });

                const data = await response.json();

                if (response.ok) {
                    // ✅ Store session details
                    sessionStorage.setItem("loggedInUser", JSON.stringify(data));
                    sessionStorage.setItem("userId", data.id); // Store user ID
                    sessionStorage.setItem("userRole", data.role); // Store user role

                    alert("Login successful! Redirecting...");

                    // ✅ Redirect based on user role
                    if (data.role === "cus") {
                        window.location.href = "/dashboard.html"; // Redirect Customers to Dashboard
                    } else if (data.role === "adm") {
                        window.location.href = "./admin.html"; // Redirect Admins to Admin Panel
                    } else {
                        window.location.href = "index.html"; // Default Redirect (Failsafe)
                    }
                } else {
                    alert(data.message || "Invalid email or password!");
                }
            } catch (error) {
                console.error("Error logging in:", error);
                alert("An error occurred. Please try again.");
            }
        });
    }

    // 🔹 Handle User Registration (Connect to Backend)
    if (registerForm) {
        registerForm.addEventListener("submit", async function (event) {
            event.preventDefault();

            const name = document.getElementById("name").value;
            const username = document.getElementById("username").value;
            const email = document.getElementById("email").value;
            const phone = document.getElementById("phone").value || null;
            const nic = document.getElementById("nic").value || null;
            const address = document.getElementById("address").value || null;
            const password = document.getElementById("password").value;
            const role = "cus"; // ✅ Default role is 'cus'

            const userData = { name, username, email, phone, nic, address, password, role };

            try {
                const response = await fetch(`${API_BASE_URL}/create`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(userData),
                });

                const data = await response.json();

                if (response.ok) {
                    alert("Registration successful! Redirecting to login...");
                    window.location.href = "login.html";
                } else {
                    alert(data.message || "Registration failed!");
                }
            } catch (error) {
                console.error("Error registering user:", error);
                alert("An error occurred. Please try again.");
            }
        });
    }

    // 🔹 Handle Logout
    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            sessionStorage.clear(); // ✅ Clear session storage
            alert("Logged out successfully!");
            window.location.href = "login.html";
        });
    }
});
