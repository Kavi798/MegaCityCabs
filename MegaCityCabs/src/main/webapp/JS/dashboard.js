document.addEventListener("DOMContentLoaded", function () {
    const logoutButton = document.getElementById("logoutLink");

    const sections = {
        home: document.getElementById("homeSection"),
        bookRide: document.getElementById("bookRideSection"),
        history: document.getElementById("historySection"),
        profile: document.getElementById("profileSection"),
    };

    const links = {
        home: document.getElementById("homeLink"),
        bookRide: document.getElementById("bookRideLink"),
        history: document.getElementById("historyLink"),
        profile: document.getElementById("profileLink"),
    };

    // Retrieve logged-in user from sessionStorage
    const loggedInUser = JSON.parse(sessionStorage.getItem("loggedInUser"));

    if (!loggedInUser) {
        window.location.href = "login.html"; // Redirect if not logged in
    } else {
       // ✅ Populate customer profile details
        document.getElementById("customerName").textContent = loggedInUser.name || "Customer";
        document.getElementById("profileUsername").textContent = loggedInUser.username || "N/A";
        document.getElementById("profileEmail").textContent = loggedInUser.email || "N/A";
        document.getElementById("profileAddress").textContent = loggedInUser.address || "N/A";
        document.getElementById("profilePhone").textContent = loggedInUser.phone || "N/A";
    }


    // Function to show the selected section and hide others
    function showSection(sectionKey) {
        Object.values(sections).forEach(section => {
            section.classList.remove("active"); // Hide all sections
        });
        sections[sectionKey].classList.add("active"); // Show the selected section
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
        sessionStorage.clear(); // ✅ Clear session storage upon logout
        alert("Logged out successfully!");
        window.location.href = "login.html";
    });

    // Default view when the dashboard loads
    showSection("home");
});
