document.addEventListener("DOMContentLoaded", function () {
    const API_BASE_URL = "http://localhost:8080/ServiceBackend/api";
    const logoutButton = document.getElementById("logoutLink");
    const customerNameSpan = document.getElementById("customerName");

    // ✅ Retrieve logged-in user from sessionStorage
    const loggedInUser = JSON.parse(sessionStorage.getItem("loggedInUser"));

    // ✅ Redirect to login if not logged in
    if (!loggedInUser) {
        window.location.href = "login.html";
        return; // Stop further execution
    }

    // ✅ Populate customer name if present
    if (customerNameSpan) {
        customerNameSpan.textContent = loggedInUser.name || "Customer";
    }

    // ✅ Logout functionality using SweetAlert2
    if (logoutButton) {
        logoutButton.addEventListener("click", function (e) {
            e.preventDefault();
            Swal.fire({
                title: 'Are you sure?',
                text: "You will be logged out!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Yes, Logout',
                cancelButtonText: 'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    sessionStorage.clear(); // Clear session
                    Swal.fire('Logged out!', 'You have been successfully logged out.', 'success').then(() => {
                        window.location.href = "login.html"; // Redirect to login
                    });
                }
            });
        });
    }

    // ✅ Handle Book a Ride (only if form exists in page)
    const bookingForm = document.getElementById("bookingForm");
    if (bookingForm) {
        bookingForm.addEventListener("submit", function (e) {
            e.preventDefault();

            const pickup = document.getElementById("pickup").value;
            const dropoff = document.getElementById("dropoff").value;
            const pickupDate = document.getElementById("pickupDate").value; // ✅ Use directly without formatting
            const vehicleType = document.getElementById("vehicleType").value;

            const bookingData = {
                userId: loggedInUser.id,
                pickupLocation: pickup,
                dropoffLocation: dropoff,
                pickupDate: pickupDate, // ✅ Correct format (yyyy-MM-dd)
                vehicleType: vehicleType
            };

            console.log("Booking Data to send:", bookingData); // ✅ Check in browser console

            // ✅ FINAL FIXED FETCH PART
            fetch(`${API_BASE_URL}/bookings/create`, {// ✅ Correct
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(bookingData)
            })
                    .then(response => response.json())
                    .then(data => {
                        if (data.id && data.id !== -1) {
                            Swal.fire('Success!', 'Your ride has been booked successfully!', 'success');
                            bookingForm.reset();
                        } else {
                            Swal.fire('No Availability', 'No available drivers or vehicles. Please try later.', 'warning');
                        }
                    })
                    .catch(error => {
                        console.error("Booking Error:", error);
                        Swal.fire('Error', 'Something went wrong. Please try later.', 'error');
                    });
        });
    }

    // ✅ Handle Booking History (only if bookingHistory div exists in page)
    const bookingHistoryDiv = document.getElementById('bookingHistory');
    if (bookingHistoryDiv) {
        loadBookingHistory(); // Call to load history
    }

//  load booking history
    function loadBookingHistory() {
        fetch(`${API_BASE_URL}/bookings/history/${loggedInUser.id}`)
                .then(response => response.json())
                .then(bookings => {
                    if (bookings.length === 0) {
                        bookingHistoryDiv.innerHTML = "<p>No bookings found.</p>";
                        return;
                    }

                    let historyHTML = "<table><tr><th>ID</th><th>Pickup</th><th>Drop-off</th><th>Date</th><th>Status</th><th>Fare</th><th>Action</th></tr>";
                    bookings.forEach(booking => {
                        historyHTML += `<tr>
                    <td>${booking.id}</td>
                    <td>${booking.pickupLocation}</td>
                    <td>${booking.dropoffLocation}</td>
                    <td>${booking.pickupDate}</td>
                    <td>${booking.bstatus}</td>
                    <td>Rs. ${booking.fare || 'Pending'}</td>
                    <td>${(booking.bstatus === 'pending')
                                ? `<button onclick="cancelBooking(${booking.id})" class="cancel-btn">Cancel</button>`
                                : '-'}</td>
                </tr>`;
                    });
                    historyHTML += "</table>";
                    bookingHistoryDiv.innerHTML = historyHTML;
                })
                .catch(error => console.error("Error loading history:", error));
    }

    // Define the function
    function cancelBooking(bookingId) {
        Swal.fire({
            title: 'Are you sure?',
            text: "You want to cancel this booking!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes, Cancel it!',
            cancelButtonText: 'No, Keep it'
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(`${API_BASE_URL}/bookings/cancel/${bookingId}`, {
                    method: "PUT"
                })
                        .then(response => response.json())
                        .then(data => {
                            Swal.fire('Cancelled!', data.message, 'success');
                            loadBookingHistory(); // Reload bookings to reflect changes
                        })
                        .catch(error => {
                            console.error("Error cancelling booking:", error);
                            Swal.fire('Error', 'Failed to cancel booking. Please try again.', 'error');
                        });
            }
        });
    }

// ✅ Attach to window for global access
    window.cancelBooking = cancelBooking;



    // ✅ Handle Profile (only if profile spans exist in page)
    const profileUsername = document.getElementById("profileUsername");
    const profileEmail = document.getElementById("profileEmail");
    const profileAddress = document.getElementById("profileAddress");
    const profilePhone = document.getElementById("profilePhone");

    if (profileUsername && profileEmail && profileAddress && profilePhone) {
        profileUsername.textContent = loggedInUser.username;
        profileEmail.textContent = loggedInUser.email;
        profileAddress.textContent = loggedInUser.address || "N/A";
        profilePhone.textContent = loggedInUser.phone || "N/A";
    }

    // ✅ Handle Edit Profile Button Click
    const editProfileButton = document.getElementById("editProfileButton");
    if (editProfileButton) {
        editProfileButton.addEventListener("click", function () {
            // Create a form for the SweetAlert popup
            Swal.fire({
                title: "Edit Profile",
                html: `
                    <form id="updateProfileForm">
                        <label for="updateName">Name:</label>
                        <input type="text" id="updateName" value="${loggedInUser.name || ""}" required><br>

                        <label for="updateAddress">Address:</label>
                        <input type="text" id="updateAddress" value="${loggedInUser.address || ""}" required><br>

                        <label for="updatePhone">Phone:</label>
                        <input type="text" id="updatePhone" value="${loggedInUser.phone || ""}" required><br>
                    </form>
                `,
                showCancelButton: true,
                confirmButtonText: "Save Changes",
                cancelButtonText: "Cancel",
                focusConfirm: false,
                preConfirm: () => {
                    const updatedUser = {
                        id: loggedInUser.id,
                        name: document.getElementById("updateName").value,
                        address: document.getElementById("updateAddress").value,
                        phone: document.getElementById("updatePhone").value,
                        role: loggedInUser.role, // Keep the role unchanged
                        email: loggedInUser.email, // Keep the email unchanged
                        username: loggedInUser.username, // Keep the username unchanged
                        nic: loggedInUser.nic // Keep the NIC unchanged
                    };

                    return fetch(`${API_BASE_URL}/users/${loggedInUser.id}`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(updatedUser)
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (!data.id) { // Check if the response contains the updated user
                            throw new Error("Failed to update profile.");
                        }
                        return data; // Return the updated user object
                    })
                    .catch(error => {
                        Swal.showValidationMessage(`Request failed: ${error.message}`);
                    });
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    // Update sessionStorage with the new user data
                    sessionStorage.setItem("loggedInUser", JSON.stringify(result.value));
                    Swal.fire("Success", "Profile updated successfully!", "success").then(() => {
                        window.location.reload(); // Refresh the page to reflect changes
                    });
                }
            });
        });
    }
});
