/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// Combined JavaScript for Admin Panel - Manage Users, Bookings, and Drivers

document.addEventListener("DOMContentLoaded", function () {
const logoutButton = document.getElementById("logoutLink");
        const sections = {
        home: document.getElementById("homeSection"),
                manageUsers: document.getElementById("manageUsersSection"),
                manageBookings: document.getElementById("manageBookingsSection"),
                manageDrivers: document.getElementById("manageDriversSection"),
                manageVehicles: document.getElementById("manageVehiclesSection"),
                reports: document.getElementById("reportsSection"),
        };
        // ✅ Expose showSection to global (window) FIRST!
        window.showSection = function (sectionKey) {
        Object.values(sections).forEach(section => section.classList.remove("active"));
                sections[sectionKey].classList.add("active");
        };
        const links = {
        home: document.getElementById("homeLink"),
                manageUsers: document.getElementById("manageUsersLink"),
                manageBookings: document.getElementById("manageBookingsLink"),
                manageDrivers: document.getElementById("manageDriversLink"),
                manageVehicles: document.getElementById("manageVehiclesLink"),
                reports: document.getElementById("reportsLink"),
        };
        document.getElementById("manageBookingsBtn").addEventListener("click", function () {
showSection("manageBookings");
        });
        document.getElementById("manageDriversBtn").addEventListener("click", function () {
showSection("manageDrivers");
        });
        document.getElementById("manageVehiclesBtn").addEventListener("click", function () {
showSection("manageVehicles");
        });
        document.getElementById("manageUsersBtn").addEventListener("click", function () {
showSection("manageUsers");
        });
        // ✅ Vehicle Modal Elements Initialization
        const vehicleModal = document.getElementById("vehicleModal");
        const closeVehicleModal = document.getElementById("closeVehicleModal");
        const cancelVehicle = document.getElementById("cancelVehicle");
        const API_BASE_URL = "http://localhost:8080/ServiceBackend/api";
        const loggedInUser = JSON.parse(sessionStorage.getItem("loggedInUser"));
        if (!loggedInUser || loggedInUser.role !== "adm") {
window.location.href = "login.html";
        } else {
document.getElementById("adminName").textContent = loggedInUser.name;
        loadUsers();
        loadBookings();
        loadDrivers();
        }

links.manageVehicles.addEventListener("click", (e) => {
e.preventDefault();
        showSection("manageVehicles");
        });
        Object.keys(links).forEach(key => {
links[key].addEventListener("click", (e) => {
e.preventDefault();
        showSection(key);
        });
        });
        logoutButton.addEventListener("click", function (e) {
        e.preventDefault();
                sessionStorage.clear();
                window.location.href = "login.html";
        });
// ✅ Modal Control Functions
        const driverModal = document.getElementById("driverModal");
        const closeDriverModal = document.getElementById("closeDriverModal");
        const cancelDriver = document.getElementById("cancelDriver");
        function openDriverModal(title, saveCallback) {
        document.getElementById("driverModalTitle").innerText = title;
                driverModal.style.display = "block";
                document.getElementById("saveDriver").onclick = saveCallback;
        }

function closeDriver() {
driverModal.style.display = "none";
        }

closeDriverModal.onclick = closeDriver;
        cancelDriver.onclick = closeDriver;
        // =======================================
// ✅ USERS MANAGEMENT WITH MODAL + SWEETALERT
// =======================================

// ✅ Load All Users
                function loadUsers() {
                fetch(`${API_BASE_URL}/users`)
                        .then(response => response.json())
                        .then(users => {
                        const usersTable = document.querySelector("#usersTable tbody");
                                usersTable.innerHTML = "";
                                users.forEach(user => {
                                usersTable.innerHTML += `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.name}</td>
                        <td>${user.address || "N/A"}</td>
                        <td>${user.phone || "N/A"}</td>
                        <td>${user.nic || "N/A"}</td>
                        <td>${user.role}</td>
                        <td>
                            <button onclick="openUserEditModal(${user.id})" class="edit-btn">Edit</button>
                            <button onclick="deleteUser(${user.id})" class="delete-btn">Delete</button>
                        </td>
                    </tr>`;
                                });
                        });
                }

// ✅ OPEN User Edit Modal with data prefilled
        window.openUserEditModal = function (userId) {
        fetch(`${API_BASE_URL}/users`)
                .then(response => response.json())
                .then(users => {
                const user = users.find(u => u.id === userId);
                        if (!user) {
                Swal.fire('Error!', 'User not found!', 'error');
                        return;
                }

                // Fill Modal Inputs
                document.getElementById('editUsername').value = user.username;
                        document.getElementById('editEmail').value = user.email;
                        document.getElementById('editName').value = user.name;
                        document.getElementById('editAddress').value = user.address;
                        document.getElementById('editPhone').value = user.phone;
                        document.getElementById('editNic').value = user.nic;
                        document.getElementById('editRole').value = user.role;
                        // Open Modal
                        document.getElementById('editUserModal').style.display = 'block';
                        // Save Changes Button Action
                        document.getElementById('saveChanges').onclick = function () {
                const updatedUser = {
                role: document.getElementById('editRole').value,
                        name: document.getElementById('editName').value,
                        address: document.getElementById('editAddress').value,
                        phone: document.getElementById('editPhone').value,
                        nic: document.getElementById('editNic').value
                };
                        // Send PUT request
                        fetch(`${API_BASE_URL}/users/${userId}`, {
                        method: "PUT",
                                headers: { "Content-Type": "application/json" },
                                body: JSON.stringify(updatedUser)
                        })
                        .then(response => {
                        if (!response.ok)
                                throw new Error('Failed to update user');
                                return response.json();
                        })
                        .then(() => {
                        Swal.fire('Success!', 'User updated successfully!', 'success');
                                document.getElementById('editUserModal').style.display = 'none';
                                loadUsers(); // Refresh table
                        })
                        .catch(error => {
                        console.error('Error:', error);
                                Swal.fire('Error!', 'Failed to update user.', 'error');
                        });
                };
                });
        };
// ✅ DELETE User with Confirmation
                window.deleteUser = function (id) {
                Swal.fire({
                title: 'Are you sure?',
                        text: "This user will be permanently deleted!",
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Yes, delete!',
                        cancelButtonText: 'Cancel'
                }).then((result) => {
                if (result.isConfirmed) {
                fetch(`${API_BASE_URL}/users/${id}`, {
                method: "DELETE"
                })
                        .then(response => {
                        if (!response.ok) throw new Error('Failed to delete user');
                                return response.json();
                        })
                        .then(() => {
                        Swal.fire('Deleted!', 'User has been deleted.', 'success');
                                loadUsers(); // Refresh
                        })
                        .catch(error => {
                        console.error('Error:', error);
                                Swal.fire('Error!', 'Failed to delete user.', 'error');
                        });
                }
                });
                };
// ✅ Close User Edit Modal on cancel/close click
                document.getElementById("closeModal").onclick = function () {
        document.getElementById("editUserModal").style.display = "none";
        };
                document.getElementById("cancelChanges").onclick = function () {
        document.getElementById("editUserModal").style.display = "none";
        };
                // ✅ Load Drivers
                        function loadDrivers() {
                        fetch(`${API_BASE_URL}/drivers`) // ✅ Correct backticks used here
                                .then(res => res.json())
                                .then(drivers => {
                                const table = document.querySelector("#driversTable tbody");
                                        table.innerHTML = ""; // Clear previous rows

                                        drivers.forEach(driver => {
                                        table.innerHTML += `
                    <tr>
                        <td>${driver.id}</td>
                        <td>${driver.dName}</td>
                        <td>${driver.phone}</td>
                        <td>${driver.license_number || "N/A"}</td>
                        <td>${driver.nic}</td>
                        <td>${driver.dstatus}</td>
                        <td>
                            <button onclick="editDriver(${driver.id})" class="edit-btn">Edit</button>
                            <button onclick="deleteDriver(${driver.id})" class="delete-btn">Delete</button>
                        </td>
                    </tr>
                `; // ✅ Everything inside backticks
                                        });
                                })
                                .catch(error => {
                                console.error('Error loading drivers:', error);
                                        Swal.fire('Error!', 'Failed to load drivers.', 'error');
                                });
                                }

// ✅ Add Driver Button
                document.getElementById("addDriverBtn").onclick = function () {
                resetDriverForm();
                        openDriverModal("Add Driver", function () {
                        const driver = getDriverFormData();
                                fetch(`${API_BASE_URL}/drivers/create`, { // ✅ Fixed URL
                                method: "POST",
                                        headers: { "Content-Type": "application/json" },
                                        body: JSON.stringify(driver)
                                })
                                .then(response => {
                                if (!response.ok) throw new Error('Failed to add driver');
                                        return response.json(); // ✅ Proper return
                                })
                                .then(() => {
                                Swal.fire('Success!', 'Driver added successfully!', 'success');
                                        loadDrivers();
                                        closeDriver();
                                })
                                .catch(error => {
                                console.error('Error:', error);
                                        Swal.fire('Error!', 'Failed to add driver.', 'error');
                                });
                        });
                        };
// ✅ Edit Driver
                        window.editDriver = function (id) {
                        fetch(`${API_BASE_URL}/drivers/${id}`) // ✅ Proper URL
                                .then(res => res.json())
                                .then(driver => {
                                fillDriverForm(driver); // Fill form with driver data
                                        openDriverModal("Edit Driver", function () {
                                        const updatedDriver = getDriverFormData();
                                                fetch(`${API_BASE_URL}/drivers/${id}`, { // ✅ Proper URL
                                                method: "PUT",
                                                        headers: { "Content-Type": "application/json" },
                                                        body: JSON.stringify(updatedDriver)
                                                })
                                                .then(response => {
                                                if (!response.ok) throw new Error('Failed to update driver'); // ✅ Proper return
                                                        return response.json();
                                                })
                                                .then(() => {
                                                Swal.fire('Success!', 'Driver updated successfully!', 'success');
                                                        loadDrivers(); // ✅ Refresh drivers
                                                        closeDriver(); // ✅ Close modal
                                                })
                                                .catch(error => {
                                                console.error('Error:', error);
                                                        Swal.fire('Error!', 'Failed to update driver.', 'error');
                                                });
                                        });
                                });
                                };
                        // ✅ Delete Driver
                        window.deleteDriver = function (id) {
                        Swal.fire({
                        title: 'Are you sure?',
                                text: "This action cannot be undone!",
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonText: 'Yes, delete it!',
                                cancelButtonText: 'Cancel'
                        }).then((result) => {
                        if (result.isConfirmed) {
                        fetch(`${API_BASE_URL}/drivers/${id}`, { // ✅ Correct URL interpolation
                        method: "DELETE"
                        })
                                .then(response => {
                                if (!response.ok) throw new Error('Failed to delete driver'); // ✅ Proper error handling
                                        return response.json(); // ✅ Proper return placement
                                })
                                .then(() => {
                                Swal.fire('Deleted!', 'Driver has been deleted.', 'success');
                                        loadDrivers(); // ✅ Reload drivers list
                                })
                                .catch(error => {
                                console.error('Error:', error);
                                        Swal.fire('Error!', 'Failed to delete driver.', 'error');
                                });
                                }
                        });
                        };
                        // ✅ Form Helpers
                                function getDriverFormData() {
                                return {
                                dName: document.getElementById("driverName").value,
                                        phone: document.getElementById("driverPhone").value,
                                        license_number: document.getElementById("driverLicense").value,
                                        nic: document.getElementById("driverNIC").value,
                                        dstatus: document.getElementById("driverStatus").value
                                };
                                }

                        function resetDriverForm() {
                        document.getElementById("driverName").value = "";
                                document.getElementById("driverPhone").value = "";
                                document.getElementById("driverLicense").value = "";
                                document.getElementById("driverNIC").value = "";
                                document.getElementById("driverStatus").value = "available";
                        }

                        function fillDriverForm(driver) {
                        document.getElementById("driverName").value = driver.dName;
                                document.getElementById("driverPhone").value = driver.phone;
                                document.getElementById("driverLicense").value = driver.license_number;
                                document.getElementById("driverNIC").value = driver.nic;
                                document.getElementById("driverStatus").value = driver.dstatus;
                        }

// ✅ Open Driver Modal with dynamic title and save function
                        function openDriverModal(title, saveCallback) {
                        document.getElementById("driverModalTitle").innerText = title;
                                driverModal.style.display = "block";
                                document.getElementById("saveDriver").onclick = saveCallback; // Save button action
                        }

// ✅ Close Modal function
                        function closeDriver() {
                        driverModal.style.display = "none";
                        }

// Close Modal on Cancel or Close (X) click
                        document.getElementById("closeDriverModal").onclick = closeDriver;
                                document.getElementById("cancelDriver").onclick = closeDriver;
// =======================================
// ✅ BOOKINGS MANAGEMENT WITH BILLING
// =======================================

// ✅ Load All Bookings with Assign Button
                                function loadBookings() {
                                fetch(`${API_BASE_URL}/bookings`) // ✅ Correct URL with backticks
                                        .then(response => response.json())
                                        .then(bookings => {
                                        const bookingsTable = document.querySelector("#bookingsTable tbody");
                                                bookingsTable.innerHTML = ""; // Clear previous rows

                                                bookings.forEach(booking => {
                                                bookingsTable.innerHTML += `
                    <tr>
                        <td>${booking.id}</td>
                        <td>${booking.customerName}</td>
                        <td>${booking.pickupLocation}</td>
                        <td>${booking.dropoffLocation}</td>
                        <td>${booking.pickupDate}</td> <!-- ✅ Pickup Date -->
                        <td>${booking.vehicleType}</td> <!-- ✅ Vehicle Type -->
                        <td>${booking.bstatus}</td>
                        <td>
                            <button onclick="updateBookingStatus(${booking.id}, '${booking.bstatus}')" class="edit-btn">Update</button>
                            <button onclick="generateBill(${booking.id})" class="bill-btn">Bill</button>
                            <button onclick="assignVehicleAndDriverToBooking(${booking.id}, '${booking.vehicleType}')" class="assign-btn">Assign Vehicle & Driver</button>
                        </td>
                    </tr>
                `; // ✅ Correct use of backticks for multiline HTML
                                                });
                                        })
                                        .catch(error => {
                                        console.error('Error loading bookings:', error);
                                                Swal.fire('Error!', 'Failed to load bookings.', 'error');
                                        });
                                        }


// ✅ Update Booking Status with SweetAlert Dropdown
                        window.updateBookingStatus = function (bookingId, currentStatus) {
                        const statusOptions = {
                        'pending': { 'accepted': 'Accepted', 'cancelled': 'Cancelled' },
                                'accepted': { 'completed': 'Completed', 'cancelled': 'Cancelled' },
                                'completed': {}, // No changes allowed
                                'cancelled': {}  // No changes allowed
                        };
                                const inputOptions = statusOptions[currentStatus];
                                if (Object.keys(inputOptions).length === 0) {
                        Swal.fire('Not Allowed!', 'This booking cannot be updated further.', 'info');
                                return;
                        }

                        Swal.fire({
                        title: 'Update Booking Status',
                                input: 'select',
                                inputOptions: inputOptions,
                                inputPlaceholder: 'Select new status',
                                showCancelButton: true,
                                confirmButtonText: 'Update',
                                cancelButtonText: 'Cancel'
                        }).then((result) => {
                        if (result.isConfirmed) {
                        const newStatus = result.value;
                                fetch(`${API_BASE_URL}/bookings/${bookingId}`, { // ✅ Correct URL
                                method: "PUT",
                                        headers: { "Content-Type": "application/json" },
                                        body: JSON.stringify({ status: newStatus })
                                })
                                .then(response => response.json())
                                .then(data => {
                                Swal.fire('Updated!', data.message, 'success');
                                        loadBookings(); // ✅ Refresh booking list
                                })
                                .catch(error => {
                                console.error('Error updating booking:', error);
                                        Swal.fire('Error!', 'Failed to update booking.', 'error');
                                });
                        }
                        });
                                };
// ✅ Generate Bill and Show Pop-Up with SweetAlert
                                window.generateBill = function (bookingId) {
                                fetch(`${API_BASE_URL}/bookings/generate-bill/${bookingId}`) // ✅ Correct URL formation
                                        .then(response => response.json())
                                        .then(bill => {
                                        Swal.fire({
                                        title: 'Booking Bill',
                                                html: `
                    <p><b>Customer Name:</b> ${bill.customerName}</p>
                    <p><b>Address:</b> ${bill.customerAddress}</p>
                    <p><b>Phone:</b> ${bill.customerPhone}</p>
                    <p><b>Pickup Location:</b> ${bill.pickupLocation}</p>
                    <p><b>Drop-off Location:</b> ${bill.dropoffLocation}</p>
                    <p><b>Total Fare:</b> Rs. ${bill.totalFare.toFixed(2)}</p>
                `,
                                                icon: 'info',
                                                showCancelButton: true,
                                                confirmButtonText: 'OK',
                                                cancelButtonText: 'Cancel'
                                        });
                                        })
                                        .catch(error => {
                                        console.error('Error generating bill:', error);
                                                Swal.fire('Error!', 'Failed to generate bill.', 'error');
                                        });
                                        };
                                <!--    // Example popup trigger-->
document.querySelectorAll('.bill-btn').forEach(btn => {
                                btn.addEventListener('click', () => {
                                Swal.fire('Billing', 'Bill will be generated here.', 'info');
                                });
});

<!--// ✅ Assign Vehicle to Booking-->
window.assignVehicleToBooking = function (bookingId, vehicleType) {
                                // Fetch available vehicles filtered by type
                                fetch(`${API_BASE_URL}/vehicles/available/${vehicleType}`) // ✅ Proper URL formation
                                .then(response => response.json())
                                .then(vehicles => {
                                let options = vehicles.map(v => `<option value="${v.id}">${v.model} (${v.plate_number})</option>`).join(''); // ✅ Correct option string
                                        Swal.fire({
                                        title: 'Assign Vehicle',
                                                html: `<select id="vehicleSelect" class="swal2-input">${options}</select>`, // ✅ Correct select with options
                                                showCancelButton: true,
                                                confirmButtonText: 'Assign Vehicle',
                                                cancelButtonText: 'Cancel'
                                        }).then((result) => {
                                if (result.isConfirmed) {
                                const vehicleId = document.getElementById('vehicleSelect').value;
                                        fetch(`${API_BASE_URL}/bookings/assign-vehicle/${bookingId}`, { // ✅ Correct fetch URL
                                        method: "PUT",
                                                headers: { "Content-Type": "application/json" },
                                                body: JSON.stringify({ vehicleId: parseInt(vehicleId) })
                                        })
                                        .then(response => response.json())
                                        .then(data => {
                                        Swal.fire('Success!', data.message, 'success');
                                                loadBookings(); // ✅ Refresh bookings
                                        })
                                        .catch(error => {
                                        console.error('Error:', error);
                                                Swal.fire('Error!', 'Failed to assign vehicle.', 'error');
                                        });
                                }
                                });
                                });
};

// ✅ Load Available Vehicles for Assigning to Booking
function loadAvailableVehicles(callback) {
                                fetch(`${API_BASE_URL}/vehicles`) // ✅ Correct URL formatting
                                .then(response => response.json())
                                .then(vehicles => {
                                const availableVehicles = vehicles.filter(v => v.status === "available"); // ✅ Filter available
                                        callback(availableVehicles); // ✅ Callback to handle available vehicles
                                })
                                .catch(error => {
                                console.error('Error loading vehicles:', error);
                                        Swal.fire('Error!', 'Failed to load available vehicles.', 'error'); // ✅ Error popup
                                });
}

// ✅ Assign Vehicle to Booking with Dropdown Selection and Warning Handling
window.assignVehicleToBooking = function (bookingId) {
                                loadAvailableVehicles(function (vehicles) {
                                let options = vehicles.map(v => `<option value="${v.id}">${v.model} (${v.plateNumber})</option>`).join("");
                                        Swal.fire({
                                        title: 'Assign Vehicle',
                                                html: `<select id="vehicleSelect" class="swal2-input">${options}</select>`,
                                                showCancelButton: true,
                                                confirmButtonText: 'Assign',
                                                cancelButtonText: 'Cancel'
                                        }).then((result) => {
                                if (result.isConfirmed) {
                                const selectedVehicleId = document.getElementById("vehicleSelect").value;
                                        fetch(`${API_BASE_URL}/bookings/assign-vehicle/${bookingId}`, { // ✅ Correct URL
                                        method: "PUT",
                                                headers: { "Content-Type": "application/json" },
                                                body: JSON.stringify({ vehicleId: parseInt(selectedVehicleId) })
                                        })
                                        .then(response => response.json().then(data => ({ status: response.status, body: data })))
                                        .then(({ status, body }) => {
                                        if (status === 200) {
                                        Swal.fire('Success!', body.message, 'success');
                                        } else if (status === 400 || status === 404) {
                                        Swal.fire('Warning!', body.message, 'warning'); // ⚠️ Show warning
                                        } else {
                                        Swal.fire('Error!', 'Unexpected error occurred.', 'error'); // ❌ Other errors
                                        }
                                        loadBookings(); // ✅ Refresh bookings
                                        })
                                        .catch(error => {
                                        console.error('Error assigning vehicle:', error);
                                                Swal.fire('Error!', 'Failed to assign vehicle.', 'error');
                                        });
                                }
                                });
                                });
};

<!--// ✅ Load All Vehicles (without filters)-->
function loadVehicles() {
                                fetch(`${API_BASE_URL}/vehicles`) // ✅ Proper backticks and slash
                                .then(res => res.json())
                                .then(vehicles => {
                                displayVehicles(vehicles); // Assuming this function displays the vehicles
                                })
                                .catch(error => {
                                console.error('Error loading vehicles:', error);
                                        Swal.fire('Error!', 'Failed to load vehicles.', 'error'); // Optional error handling
                                });
}
<!--// ✅ Display Vehicles Helper-->
function displayVehicles(vehicles) {
                                const table = document.querySelector("#vehiclesTable tbody");
                                table.innerHTML = ""; // Clear existing rows
                                vehicles.forEach(vehicle => {
                                table.innerHTML += `
            <tr>
                <td>${vehicle.id}</td>
                <td>${vehicle.model}</td>
                <td>${vehicle.plateNumber}</td>
                <td>${vehicle.capacity}</td>
                <td>${vehicle.type}</td>
                <td>${vehicle.status}</td>
                <td>
                    <button onclick="editVehicle(${vehicle.id})" class="edit-btn">Edit</button>
                    <button onclick="deleteVehicle(${vehicle.id})" class="delete-btn">Delete</button>
                </td>
            </tr>
        `;
                                });
}

<!--// ✅ Filter Function-->
document.getElementById("filterVehiclesBtn").onclick = function () {
                                const type = document.getElementById("vehicleTypeFilter").value;
                                const status = document.getElementById("vehicleStatusFilter").value;
                                const plateNumber = document.getElementById("vehiclePlateFilter").value;
                                fetch(`${API_BASE_URL}/vehicles`) // ✅ Correct usage
                                .then(res => res.json())
                                .then(vehicles => {
                                let filtered = vehicles;
                                        if (type)
                                        filtered = filtered.filter(v => v.type === type);
                                        if (status)
                                        filtered = filtered.filter(v => v.status === status);
                                        if (plateNumber)
                                        filtered = filtered.filter(v => v.plateNumber.includes(plateNumber));
                                        displayVehicles(filtered); // ✅ Display filtered result
                                })
                                .catch(error => {
                                console.error('Error filtering vehicles:', error);
                                        Swal.fire('Error!', 'Failed to filter vehicles.', 'error');
                                });
};

<!--// ✅ Reset Filter Button-->
document.getElementById("resetFilterBtn").onclick = function () {
                                // Clear Filter Fields
                                document.getElementById("vehicleTypeFilter").value = "";
                                document.getElementById("vehicleStatusFilter").value = "";
                                document.getElementById("vehiclePlateFilter").value = "";
                                loadVehicles(); // Reload All Vehicles
};

<!--// ✅ Add Vehicle Button-->
document.getElementById("addVehicleBtn").onclick = function () {
                                resetVehicleForm();
                                openVehicleModal("Add Vehicle", function () {
                                const vehicle = getVehicleFormData();
                                        fetch(`${API_BASE_URL}/vehicles/create`, {  // ✅ Corrected URL
                                        method: "POST",
                                                headers: { "Content-Type": "application/json" },
                                                body: JSON.stringify(vehicle)
                                        })
                                        .then(response => {
                                        if (!response.ok) throw new Error('Failed to add vehicle');
                                                return response.json();
                                        })
                                        .then(() => {
                                        Swal.fire('Success!', 'Vehicle added successfully!', 'success');
                                                loadVehicles(); // ✅ Refresh vehicle list
                                                closeVehicle(); // ✅ Close modal
                                        })
                                        .catch(error => {
                                        console.error('Error:', error);
                                                Swal.fire('Error!', 'Failed to add vehicle.', 'error');
                                        });
                                });
};

<!--// ✅ Delete Vehicle-->
window.deleteVehicle = function (id) {
                                Swal.fire({
                                title: 'Are you sure?',
                                        text: "This action cannot be undone!",
                                        icon: 'warning',
                                        showCancelButton: true,
                                        confirmButtonText: 'Yes, delete it!',
                                        cancelButtonText: 'Cancel'
                                }).then((result) => {
                        if (result.isConfirmed) {
                        fetch(`${API_BASE_URL}/vehicles/${id}`, { method: "DELETE" })  // ✅ Corrected URL
                                .then(response => {
                                if (!response.ok) throw new Error('Failed to delete vehicle');
                                        return response.json();
                                })
                                .then(() => {
                                Swal.fire('Deleted!', 'Vehicle has been deleted.', 'success');
                                        loadVehicles(); // ✅ Reload updated list
                                })
                                .catch(error => {
                                console.error('Error:', error);
                                        Swal.fire('Error!', 'Failed to delete vehicle.', 'error');
                                });
                        }
                        });
};

<!--// ✅ Edit Vehicle-->
window.editVehicle = function (id) {
                                fetch(`${API_BASE_URL}/vehicles`) // ✅ Correct URL to get all vehicles or adjust if specific endpoint exists
                                .then(res => res.json())
                                .then(vehicles => {
                                const vehicle = vehicles.find(v => v.id === id);
                                        if (!vehicle) {
                                Swal.fire('Error!', 'Vehicle not found!', 'error');
                                        return;
                                }

                                // Fill modal fields
                                document.getElementById("vehicleModel").value = vehicle.model;
                                        document.getElementById("vehiclePlate").value = vehicle.plateNumber;
                                        document.getElementById("vehicleCapacity").value = vehicle.capacity;
                                        document.getElementById("vehicleType").value = vehicle.type;
                                        document.getElementById("vehicleStatus").value = vehicle.status;
                                        // Open Modal
                                        openVehicleModal("Edit Vehicle", function () {
                                        const updatedVehicle = getVehicleFormData();
                                                fetch(`${API_BASE_URL}/vehicles/${id}`, { // ✅ Corrected URL
                                                method: "PUT",
                                                        headers: { "Content-Type": "application/json" },
                                                        body: JSON.stringify(updatedVehicle)
                                                })
                                                .then(response => {
                                                if (!response.ok) throw new Error('Failed to update vehicle');
                                                        return response.json();
                                                })
                                                .then(() => {
                                                Swal.fire('Success!', 'Vehicle updated successfully!', 'success');
                                                        closeVehicle();
                                                        loadVehicles(); // Refresh
                                                })
                                                .catch(error => {
                                                console.error('Error:', error);
                                                        Swal.fire('Error!', 'Failed to update vehicle.', 'error');
                                                });
                                        });
                                });
};

document.getElementById("filterVehiclesBtn").onclick = function () {
                                const type = document.getElementById("vehicleTypeFilter").value;
                                const status = document.getElementById("vehicleStatusFilter").value;
                                const plateNumber = document.getElementById("vehiclePlateFilter").value;
                                fetch(`${API_BASE_URL}/vehicles`) // ✅ Correct template literal
                                .then(res => res.json())
                                .then(vehicles => {
                                let filtered = vehicles;
                                        if (type) filtered = filtered.filter(v => v.type === type);
                                        if (status) filtered = filtered.filter(v => v.status === status);
                                        if (plateNumber) filtered = filtered.filter(v => v.plateNumber.includes(plateNumber));
                                        const table = document.querySelector("#vehiclesTable tbody");
                                        table.innerHTML = ""; // Clear previous data

                                        filtered.forEach(vehicle => {
                                        table.innerHTML += `
                    <tr>
                        <td>${vehicle.id}</td>
                        <td>${vehicle.model}</td>
                        <td>${vehicle.plateNumber}</td>
                        <td>${vehicle.capacity}</td>
                        <td>${vehicle.type}</td>
                        <td>${vehicle.status}</td>
                        <td>
                            <button onclick="editVehicle(${vehicle.id})" class="edit-btn">Edit</button>
                            <button onclick="deleteVehicle(${vehicle.id})" class="delete-btn">Delete</button>
                        </td>
                    </tr>`; // ✅ Correct usage of backticks
                                        });
                                });
};

<!--// Modal Functions-->
function openVehicleModal(title, saveCallback) {
                                document.getElementById("vehicleModalTitle").innerText = title;
                                vehicleModal.style.display = "block";
                                document.getElementById("saveVehicle").onclick = saveCallback;
}

function closeVehicle() {
                                vehicleModal.style.display = "none";
}

closeVehicleModal.onclick = closeVehicle;
cancelVehicle.onclick = closeVehicle;

function resetVehicleForm() {
                                document.getElementById("vehicleModel").value = "";
                                document.getElementById("vehiclePlate").value = "";
                                document.getElementById("vehicleCapacity").value = "";
                                document.getElementById("vehicleType").value = "";
                                document.getElementById("vehicleStatus").value = "available";
}

function getVehicleFormData() {
                                let capacity = parseInt(document.getElementById("vehicleCapacity").value);
                                if (isNaN(capacity) || capacity < 1) {
                        Swal.fire('Error!', 'Capacity must be a positive number.', 'error');
                                throw new Error("Invalid capacity value"); // Prevent submission
                        }

                        return {
                        model: document.getElementById("vehicleModel").value.trim(),
                                plateNumber: document.getElementById("vehiclePlate").value.trim(),
                                capacity: capacity,
                                type: document.getElementById("vehicleType").value.trim(),
                                status: document.getElementById("vehicleStatus").value
                        };
}

// ✅ Assign Vehicle and Driver to Booking (Combined Popup)
window.assignVehicleAndDriverToBooking = function (bookingId, vehicleType) {

                                // Fetch available vehicles by type
                                fetch(`${API_BASE_URL}/vehicles/available/${vehicleType}`)
                                .then(response => response.json())
                                .then(vehicles => {

                                // Fetch available drivers
                                fetch(`${API_BASE_URL}/drivers/available`)
                                        .then(response => response.json())
                                        .then(drivers => {

                                        if (vehicles.length === 0 || drivers.length === 0) {
                                        Swal.fire('Warning!', 'No available vehicles or drivers found!', 'warning');
                                                return;
                                        }

                                        // Prepare vehicle options
                                        let vehicleOptions = vehicles.map(v => `<option value="${v.id}">${v.model} (${v.plate_number})</option>`).join('');
                                                // Prepare driver options
                                                let driverOptions = drivers.map(d => `<option value="${d.id}">${d.dName} (License: ${d.license_number})</option>`).join('');
                                                // Show SweetAlert Popup for combined selection
                                                Swal.fire({
                                                title: 'Assign Vehicle & Driver',
                                                        html: `
                            <label>Select Vehicle:</label>
                            <select id="vehicleSelect" class="swal2-input">${vehicleOptions}</select>
                            <label>Select Driver:</label>
                            <select id="driverSelect" class="swal2-input">${driverOptions}</select>
                        `,
                                                        showCancelButton: true,
                                                        confirmButtonText: 'Assign',
                                                        cancelButtonText: 'Cancel',
                                                        preConfirm: () => {
                                                return {
                                                vehicleId: document.getElementById('vehicleSelect').value,
                                                        driverId: document.getElementById('driverSelect').value
                                                };
                                                }
                                                }).then((result) => {
                                        if (result.isConfirmed) {
                                        const selectedVehicleId = parseInt(result.value.vehicleId);
                                                const selectedDriverId = parseInt(result.value.driverId);
                                                // API Call to backend for assignment
                                                fetch(`${API_BASE_URL}/vehicles/assign-vehicle-driver/${bookingId}`, {
                                                method: "PUT",
                                                        headers: { "Content-Type": "application/json" },
                                                        body: JSON.stringify({ vehicleId: selectedVehicleId, driverId: selectedDriverId })
                                                })
                                                .then(response => response.json().then(data => ({ status: response.status, body: data })))
                                                .then(({ status, body }) => {
                                                if (status === 200) {
                                                Swal.fire('Success!', body.message, 'success');
                                                } else {
                                                Swal.fire('Error!', body.message, 'error');
                                                }
                                                loadBookings(); // ✅ Refresh Bookings Table
                                                })
                                                .catch(error => {
                                                console.error('Error:', error);
                                                        Swal.fire('Error!', 'Failed to assign vehicle and driver.', 'error');
                                                });
                                        }
                                        });
                                        })
                                        .catch(error => {
                                        console.error('Error loading drivers:', error);
                                                Swal.fire('Error!', 'Failed to load available drivers.', 'error');
                                        });
                                })
                                .catch(error => {
                                console.error('Error loading vehicles:', error);
                                        Swal.fire('Error!', 'Failed to load available vehicles.', 'error');
                                });
};

<!--// Load Vehicles on Login-->
loadVehicles();


<!--    // ✅ Load Admin Dashboard Summary Data-->
function loadAdminDashboard() {
                                // Total Bookings
                                fetch(${API_BASE_URL} / bookings)
                                .then(response => response.json())
                                .then(bookings => document.getElementById("totalBookings").textContent = bookings.length);
                                // Available Drivers
                                fetch(${API_BASE_URL} / drivers / available)
                                .then(response => response.json())
                                .then(drivers => document.getElementById("availableDrivers").textContent = drivers.length);
                                // Available Vehicles
                                fetch(${API_BASE_URL} / vehicles / available)
                                .then(response => response.json())
                                .then(vehicles => document.getElementById("availableVehicles").textContent = vehicles.length);
                                // Total Customers
                                fetch(${API_BASE_URL} / users)
                                .then(response => response.json())
                                .then(users => {
                                const customers = users.filter(user => user.role === "cus");
                                        document.getElementById("totalCustomers").textContent = customers.length;
                                });
}

<!--// ✅ Call function on load-->
loadAdminDashboard();


<!--    // ✅ Default section to Home-->
showSection("home");
});