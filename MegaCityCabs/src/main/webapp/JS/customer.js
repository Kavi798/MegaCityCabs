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
            const pickupDate = document.getElementById("pickupDate").value;
            const vehicleType = document.getElementById("vehicleType").value;

            const bookingData = {
                userId: loggedInUser.id,
                pickupLocation: pickup,
                dropoffLocation: dropoff,
                pickupDate: pickupDate,
                vehicleType: vehicleType,
            };

            console.log("Booking Data:", bookingData); // Debugging

            // ✅ Step 1: Create booking and get ID
            fetch(`${API_BASE_URL}/bookings/create`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(bookingData)
            })
                    .then(response => response.json()) // Parse response JSON
                    .then(data => {
                        console.log("Booking Created Response:", data);
                        if (data.id) {
                            // ✅ Step 2: Call generate bill API with returned booking ID
                            fetch(`${API_BASE_URL}/bookings/generate-bill/${data.id}`)
                                    .then(response => response.json())
                                    .then(bill => {
                                        console.log("Generated Bill:", bill);
                                        // ✅ Step 3: Show bill in SweetAlert
                                        Swal.fire({
                                            title: 'Your Ride Bill',
                                            html: `
                                    <p><strong>Name:</strong> ${bill.customerName}</p>
                                    <p><strong>Pickup:</strong> ${bill.pickupLocation}</p>
                                    <p><strong>Drop-off:</strong> ${bill.dropoffLocation}</p>
                                    <p><strong>Vehicle Type:</strong> ${vehicleType}</p>
                                    <p><strong>Total Fare:</strong> Rs. ${bill.totalFare}</p>
                                `,
                                            icon: 'info',
                                            confirmButtonText: 'OK'
                                        });
                                        bookingForm.reset(); // ✅ Reset form after showing bill
                                    })
                                    .catch(error => {
                                        console.error("Error fetching bill:", error);
                                        Swal.fire('Error', 'Failed to generate bill. Please try later.', 'error');
                                    });
                        } else {
                            Swal.fire('Error', 'Failed to book ride. Please try again.', 'error');
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
});
