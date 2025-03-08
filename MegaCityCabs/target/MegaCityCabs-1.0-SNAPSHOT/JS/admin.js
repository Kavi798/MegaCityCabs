/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener("DOMContentLoaded", function () {
    const logoutButton = document.getElementById("logoutLink");

    const sections = {
        dashboard: document.getElementById("dashboardSection"),
        manageUsers: document.getElementById("manageUsersSection"),
        manageBookings: document.getElementById("manageBookingsSection"),
        reports: document.getElementById("reportsSection"),
    };

    const links = {
        dashboard: document.getElementById("dashboardLink"),
        manageUsers: document.getElementById("manageUsersLink"),
        manageBookings: document.getElementById("manageBookingsLink"),
        reports: document.getElementById("reportsLink"),
    };

    // Retrieve logged-in admin from sessionStorage
    const loggedInUser = JSON.parse(sessionStorage.getItem("loggedInUser"));

    if (!loggedInUser || loggedInUser.role !== "adm") {
        window.location.href = "login.html"; // Redirect if not an admin
    }

    // Function to show the selected section and hide others
    function showSection(sectionKey) {
        Object.values(sections).forEach(section => section.classList.remove("active"));
        sections[sectionKey].classList.add("active");
    }

    // Event listeners for navigation
    Object.keys(links).forEach(key => {
        links[key].addEventListener("click", (e) => {
            e.preventDefault();
            showSection(key);
        });
    });

    // Logout functionality
    logoutButton.addEventListener("click", function (e) {
        e.preventDefault();
        sessionStorage.clear();
        alert("Logged out successfully!");
        window.location.href = "login.html";
    });

    // Default view when the admin panel loads
    showSection("dashboard");
});

