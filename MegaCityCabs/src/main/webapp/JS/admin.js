// =========================
// Admin Panel Common Script
// =========================

// ✅ Logout Functionality
document.addEventListener("DOMContentLoaded", () => {
    const logoutBtn = document.getElementById('logoutLink');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            // Add logout logic (e.g., clear session, redirect to login)
            Swal.fire('Logged Out', 'You have been successfully logged out.', 'success').then(() => {
                window.location.href = 'login.html';
            });
        });
    }
});

// =========================
// Home Dashboard (admin.html)
// =========================

function loadDashboardStats() {
    // Example fetch call for counts (update endpoints accordingly)
    fetch('/api/dashboard/stats')
            .then(response => response.json())
            .then(data => {
                document.getElementById('totalBookings').innerText = data.totalBookings;
                document.getElementById('availableDrivers').innerText = data.availableDrivers;
                document.getElementById('availableVehicles').innerText = data.availableVehicles;
                document.getElementById('totalCustomers').innerText = data.totalCustomers;
            });
}

// Call only if element exists
if (document.getElementById('totalBookings'))
    loadDashboardStats();

// ======================
// Manage Users Section
// ======================

const API_BASE_URL = "http://localhost:8080/ServiceBackend/api";

// ✅ Load Users
function loadUsers() {
    fetch(`${API_BASE_URL}/users`)
            .then(response => response.json())
            .then(users => {
                const tableBody = document.querySelector('#usersTable tbody');
                tableBody.innerHTML = '';
                users.forEach(user => {
                    const row = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.name}</td>
                        <td>${user.address || '-'}</td>
                        <td>${user.phone || '-'}</td>
                        <td>${user.nic || '-'}</td>
                        <td>${user.role === 'adm' ? 'Admin' : 'Customer'}</td>
                        <td>
                            <button onclick="openEditUserModal(${user.id})">Edit</button>
                            <button onclick="deleteUser(${user.id})">Delete</button>
                        </td>
                    </tr>`;
                    tableBody.innerHTML += row;
                });
            })
            .catch(error => console.error('Error loading users:', error));
}

// ✅ Open Edit Modal
function openEditUserModal(id) {
    fetch(`${API_BASE_URL}/users/${id}`)
            .then(response => response.json())
            .then(user => {
                document.getElementById('editUsername').value = user.username;
                document.getElementById('editEmail').value = user.email;
                document.getElementById('editName').value = user.name;
                document.getElementById('editAddress').value = user.address || '';
                document.getElementById('editPhone').value = user.phone || '';
                document.getElementById('editNic').value = user.nic || '';
                document.getElementById('editRole').value = user.role;

                document.getElementById('editUserModal').style.display = 'block';
                document.getElementById('saveChanges').onclick = () => saveUserChanges(id);
            });
}

// ✅ Update User
function saveUserChanges(id) {
    const updatedUser = {
        role: document.getElementById('editRole').value,
        name: document.getElementById('editName').value,
        address: document.getElementById('editAddress').value,
        phone: document.getElementById('editPhone').value,
        nic: document.getElementById('editNic').value
    };

    fetch(`${API_BASE_URL}/users/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(updatedUser)
    })
            .then(response => response.ok ? Swal.fire('Success', 'User updated!', 'success') && loadUsers() : Swal.fire('Error', 'Failed to update user!', 'error'));
}

// ✅ Delete User
function deleteUser(id) {
    Swal.fire({
        title: 'Are you sure?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
    }).then(result => {
        if (result.isConfirmed) {
            fetch(`${API_BASE_URL}/users/${id}`, {method: 'DELETE'})
                    .then(response => response.ok ? Swal.fire('Deleted!', 'User removed.', 'success') && loadUsers() : Swal.fire('Error', 'Delete failed!', 'error'));
        }
    });
}

// ✅ Close Modal
const cancelChangesBtn = document.getElementById('cancelChanges');
if (cancelChangesBtn) {
    cancelChangesBtn.addEventListener('click', () => {
        document.getElementById('editUserModal').style.display = 'none';
    });
}

// ✅ Load Users on Page Load
if (document.getElementById('usersTable')) {
    loadUsers();
}

// ======================
// Manage Bookings Section
// ======================

// ✅ Load All Bookings
function loadBookings() {
    fetch(`${API_BASE_URL}/bookings`)
            .then(response => response.json())
            .then(bookings => {
                console.log('Bookings:', bookings); // ✅ Debugging log
                const tableBody = document.querySelector('#bookingsTable tbody');
                tableBody.innerHTML = '';
                bookings.forEach(booking => {
                    const row = `
                    <tr>
                        <td>${booking.id}</td>
                        <td>${booking.customerName}</td>
                        <td>${booking.pickupLocation}</td>
                        <td>${booking.dropoffLocation}</td>
                        <td>${booking.pickupDate}</td>
                        <td>${booking.vehicleType}</td>
                        <td>${booking.bstatus}</td>
                        <td>
                            <button onclick="viewBill(${booking.id})">View Bill</button>
                            <button onclick="changeBookingStatus(${booking.id}, '${booking.bstatus}')">Change Status</button>
                        </td>
                    </tr>`;
                    tableBody.innerHTML += row;
                });
            })
            .catch(error => console.error('Error loading bookings:', error));
}

// ✅ View Bill (Generate Bill)
function viewBill(bookingId) {
    fetch(`${API_BASE_URL}/bookings/generate-bill/${bookingId}`)
            .then(response => response.json())
            .then(bill => {
                Swal.fire({
                    title: 'Booking Bill',
                    html: `
                    <strong>Customer Name:</strong> ${bill.customerName}<br>
                    <strong>Address:</strong> ${bill.customerAddress}<br>
                    <strong>Phone:</strong> ${bill.customerPhone}<br>
                    <strong>Pickup:</strong> ${bill.pickupLocation}<br>
                    <strong>Drop-off:</strong> ${bill.dropoffLocation}<br>
                    <strong>Total Fare:</strong> Rs. ${bill.totalFare.toFixed(2)}
                `,
                    icon: 'info'
                });
            })
            .catch(error => {
                console.error('Error generating bill:', error);
                Swal.fire('Error', 'Failed to generate bill!', 'error');
            });
}

// ✅ Change Booking Status (Accept, Complete, Cancel)
function changeBookingStatus(bookingId, currentStatus) {
    let nextStatusOptions = [];

    if (currentStatus === 'pending') {
        nextStatusOptions = ['accepted', 'cancelled'];
    } else if (currentStatus === 'accepted') {
        nextStatusOptions = ['completed'];
    } else {
        Swal.fire('Info', 'No further action possible on this booking.', 'info');
        return;
    }

    Swal.fire({
        title: 'Change Status',
        input: 'select',
        inputOptions: nextStatusOptions.reduce((acc, status) => {
            acc[status] = status.charAt(0).toUpperCase() + status.slice(1);
            return acc;
        }, {}),
        inputPlaceholder: 'Select new status',
        showCancelButton: true,
    }).then(result => {
        if (result.isConfirmed && result.value) {
            const newStatus = result.value;

            fetch(`${API_BASE_URL}/bookings/${bookingId}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({status: newStatus})
            })
                    .then(response => {
                        if (response.ok) {
                            Swal.fire('Success', 'Booking status updated!', 'success');
                            loadBookings(); // Reload bookings
                        } else {
                            Swal.fire('Error', 'Failed to update status!', 'error');
                        }
                    })
                    .catch(error => console.error('Error updating status:', error));
        }
    });
}

// ✅ Load bookings on page load
if (document.getElementById('bookingsTable')) {
    loadBookings();
}

// ======================
// Manage Driver Section
// ======================

// ✅ Load All Drivers
function loadDrivers() {
    fetch(`${API_BASE_URL}/drivers`)
            .then(response => response.json())
            .then(drivers => {
                console.log("Drivers:", drivers); // ✅ Debugging log
                const tableBody = document.querySelector('#driversTable tbody');
                tableBody.innerHTML = ''; // Clear old data

                drivers.forEach(driver => {
                    const row = `
                    <tr>
                        <td>${driver.id}</td>
                        <td>${driver.dName}</td>
                        <td>${driver.phone || '-'}</td>
                        <td>${driver.licenseNumber || '-'}</td>
                        <td>${driver.nic}</td>
                        <td>${driver.dstatus}</td>
                        <td>
                            <button onclick="openDriverModal(${driver.id})">Edit</button>
                            <button onclick="deleteDriver(${driver.id})">Delete</button>
                        </td>
                    </tr>`;
                    tableBody.innerHTML += row;
                });
            })
            .catch(error => console.error('Error loading drivers:', error));
}

// ✅ Open Driver Edit Modal
function openDriverModal(id) {
    fetch(`${API_BASE_URL}/drivers/${id}`)
            .then(response => response.json())
            .then(driver => {
                console.log("Edit Driver:", driver); // ✅ Debug

                document.getElementById('editDriverName').value = driver.dName;
                document.getElementById('editDriverPhone').value = driver.phone || '';
                document.getElementById('editDriverLicense').value = driver.licenseNumber || '';
                document.getElementById('editDriverNic').value = driver.nic;
                document.getElementById('editDriverStatus').value = driver.dstatus;

                // Open Modal
                document.getElementById('editDriverModal').style.display = 'block';

                // Set Save Button Event
                document.getElementById('saveDriverChanges').onclick = () => saveDriverChanges(id);
            })
            .catch(error => console.error('Error fetching driver:', error));
}

// ✅ Save Driver Changes
function saveDriverChanges(id) {
    const updatedDriver = {
        dName: document.getElementById('editDriverName').value,
        phone: document.getElementById('editDriverPhone').value,
        licenseNumber: document.getElementById('editDriverLicense').value,
        nic: document.getElementById('editDriverNic').value,
        dstatus: document.getElementById('editDriverStatus').value
    };

    fetch(`${API_BASE_URL}/drivers/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(updatedDriver)
    })
            .then(response => {
                if (response.ok) {
                    Swal.fire('Success', 'Driver updated successfully!', 'success');
                    document.getElementById('editDriverModal').style.display = 'none';
                    loadDrivers(); // Reload drivers
                } else {
                    Swal.fire('Error', 'Failed to update driver!', 'error');
                }
            })
            .catch(error => console.error('Error updating driver:', error));
}

// ✅ Delete Driver
function deleteDriver(id) {
    Swal.fire({
        title: 'Are you sure?',
        text: "This action cannot be undone!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!'
    }).then(result => {
        if (result.isConfirmed) {
            fetch(`${API_BASE_URL}/drivers/${id}`, {
                method: 'DELETE'
            })
                    .then(response => {
                        if (response.ok) {
                            Swal.fire('Deleted!', 'Driver deleted successfully!', 'success');
                            loadDrivers(); // Reload drivers
                        } else {
                            Swal.fire('Error!', 'Failed to delete driver!', 'error');
                        }
                    })
                    .catch(error => console.error('Error deleting driver:', error));
        }
    });
}

// ✅ Close Modal Buttons
if (document.getElementById('cancelDriverChanges')) {
    document.getElementById('cancelDriverChanges').addEventListener('click', () => {
        document.getElementById('editDriverModal').style.display = 'none';
    });
}

if (document.getElementById('closeDriverModal')) {
    document.getElementById('closeDriverModal').addEventListener('click', () => {
        document.getElementById('editDriverModal').style.display = 'none';
    });
}

if (document.getElementById('addDriverBtn')) {
    document.getElementById('addDriverBtn').addEventListener('click', () => {
        document.getElementById('addDriverModal').style.display = 'block';
    });
}

if (document.getElementById('cancelAddDriver')) {
    document.getElementById('cancelAddDriver').addEventListener('click', () => {
        document.getElementById('addDriverModal').style.display = 'none';
    });
}

if (document.getElementById('closeAddDriverModal')) {
    document.getElementById('closeAddDriverModal').addEventListener('click', () => {
        document.getElementById('addDriverModal').style.display = 'none';
    });
}

// ✅ Save New Driver
const saveAddDriverBtn = document.getElementById('saveAddDriver');
if (saveAddDriverBtn) {
    saveAddDriverBtn.addEventListener('click', () => {
        const newDriver = {
            dName: document.getElementById('addDriverName').value,
            phone: document.getElementById('addDriverPhone').value,
            licenseNumber: document.getElementById('addDriverLicense').value,
            nic: document.getElementById('addDriverNic').value,
            dstatus: document.getElementById('addDriverStatus').value
        };

        fetch(`${API_BASE_URL}/drivers/create`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(newDriver)
        })
                .then(response => {
                    if (response.ok) {
                        Swal.fire('Success', 'Driver added successfully!', 'success');
                        document.getElementById('addDriverModal').style.display = 'none';
                        loadDrivers(); // ✅ Reload drivers
                    } else {
                        Swal.fire('Error', 'Failed to add driver!', 'error');
                    }
                })
                .catch(error => console.error('Error adding driver:', error));
    });
}

// ✅ Load Drivers on Page Load
if (document.getElementById('driversTable')) {
    loadDrivers();
}

// ======================
// Manage Vehicles Section
// ======================

const VEHICLE_API = `${API_BASE_URL}/vehicles`;
const vehicleModal = document.getElementById('vehicleModal');

// ✅ Load Vehicles
function loadVehicles() {
    fetch(VEHICLE_API)
            .then(response => response.json())
            .then(vehicles => renderVehicles(vehicles));
}

// ✅ Load All Drivers and Populate in Table
function loadDrivers() {
    fetch(`${API_BASE_URL}/drivers`) // Fetch from API
        .then(response => response.json())
        .then(drivers => {
            console.log("Drivers:", drivers); // Debugging log
            const tableBody = document.querySelector('#driversTable tbody');
            tableBody.innerHTML = ''; // Clear existing data

            // Loop and append each driver as a row
            drivers.forEach(driver => {
                const row = `
                    <tr>
                        <td>${driver.id}</td>
                        <td>${driver.dName}</td>
                        <td>${driver.phone || '-'}</td>
                        <td>${driver.licenseNumber}</td>
                        <td>${driver.nic}</td>
                        <td>${driver.dstatus}</td>
                        <td>
                            <button onclick="openDriverModal(${driver.id})">Edit</button>
                            <button onclick="deleteDriver(${driver.id})">Delete</button>
                        </td>
                    </tr>`;
                tableBody.innerHTML += row; // Append to table
            });
        })
        .catch(error => console.error('Error loading drivers:', error)); // Handle fetch errors
}

// ✅ Load drivers automatically when the page loads
if (document.getElementById('driversTable')) {
    loadDrivers();
}

function renderVehicles(vehicles) {
    const tableBody = document.querySelector('#vehiclesTable tbody');
    tableBody.innerHTML = '';
    vehicles.forEach(vehicle => {
        const row = `
            <tr>
                <td>${vehicle.id}</td>
                <td>${vehicle.model}</td>
                <td>${vehicle.plateNumber}</td>
                <td>${vehicle.capacity}</td>
                <td>${vehicle.type}</td>
                <td>${vehicle.status}</td>
                <td>${vehicle.driverName ? vehicle.driverName : 'None'}</td> <!-- ✅ Show Driver Name -->
                <td>
                    <button onclick="openEditVehicleModal(${vehicle.id})">Edit</button>
                    <button onclick="deleteVehicle(${vehicle.id})">Delete</button>
                </td>
            </tr>`;
        tableBody.innerHTML += row;
    });
}

// ✅ Filter Vehicles
document.getElementById('filterVehiclesBtn')?.addEventListener('click', () => {
    const type = document.getElementById('vehicleTypeFilter').value;
    const status = document.getElementById('vehicleStatusFilter').value;

    fetch(`${VEHICLE_API}/search?type=${type}&status=${status}`)
            .then(response => response.json())
            .then(vehicles => renderVehicles(vehicles));
});

// ✅ Reset Filter
document.getElementById('resetFilterBtn')?.addEventListener('click', () => {
    document.getElementById('vehicleTypeFilter').value = '';
    document.getElementById('vehicleStatusFilter').value = '';
    loadVehicles();
});

// ✅ Open Add Modal
document.getElementById('addVehicleBtn')?.addEventListener('click', () => {
    resetVehicleForm(); // Clear form fields
    loadDrivers(); // Load available drivers
    document.getElementById('vehicleModalTitle').innerText = 'Add Vehicle';
    document.getElementById('saveVehicle').onclick = saveNewVehicle;
    vehicleModal.style.display = 'block';
});

// ✅ Reset Vehicle Form
function resetVehicleForm() {
    document.getElementById('vehicleModel').value = '';
    document.getElementById('vehiclePlate').value = '';
    document.getElementById('vehicleCapacity').value = '';
    document.getElementById('vehicleType').value = 'Sedan';
    document.getElementById('vehicleStatus').value = 'available';
}

// Save New Vehicle
function saveNewVehicle() {
    const newVehicle = {
        model: document.getElementById('vehicleModel').value,
        plateNumber: document.getElementById('vehiclePlate').value,
        capacity: parseInt(document.getElementById('vehicleCapacity').value),
        type: document.getElementById('vehicleType').value,
        status: document.getElementById('vehicleStatus').value,
        driverId: document.getElementById('vehicleDriver').value || null // Optional
    };
    fetch(`${VEHICLE_API}/create`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(newVehicle)
    })
    .then(response => {
        if (response.ok) {
            Swal.fire('Success', 'Vehicle added!', 'success');
            vehicleModal.style.display = 'none';
            resetVehicleForm();
            loadVehicles();
        } else {
            Swal.fire('Error', 'Failed to add vehicle!', 'error');
        }
    });
}

function openEditVehicleModal(id) {
    resetVehicleForm();
    fetch(`${VEHICLE_API}/${id}`)
        .then(response => response.json())
        .then(vehicle => {
            loadDrivers(vehicle.driverId); // ✅ load drivers properly when editing

            document.getElementById('vehicleModel').value = vehicle.model;
            document.getElementById('vehiclePlate').value = vehicle.plateNumber;
            document.getElementById('vehicleCapacity').value = vehicle.capacity;
            document.getElementById('vehicleType').value = vehicle.type;
            document.getElementById('vehicleStatus').value = vehicle.status;

            document.getElementById('vehicleModalTitle').innerText = 'Edit Vehicle';
            document.getElementById('saveVehicle').onclick = () => saveVehicleChanges(id);
            vehicleModal.style.display = 'block';
        });
}

function saveVehicleChanges(id) {
    const updatedVehicle = {
        model: document.getElementById('vehicleModel').value,
        plateNumber: document.getElementById('vehiclePlate').value,
        capacity: parseInt(document.getElementById('vehicleCapacity').value),
        type: document.getElementById('vehicleType').value,
        status: document.getElementById('vehicleStatus').value,
        driverId: document.getElementById('vehicleDriver').value || null // Optional
    };
    fetch(`${VEHICLE_API}/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(updatedVehicle)
    }).then(response => {
        if (response.ok) {
            Swal.fire('Success', 'Vehicle updated!', 'success');
            vehicleModal.style.display = 'none';
            loadVehicles();
        } else {
            Swal.fire('Error', 'Failed to update vehicle!', 'error');
        }
    });
}

// ✅ Delete Vehicle
function deleteVehicle(id) {
    Swal.fire({
        title: 'Are you sure?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!'
    }).then(result => {
        if (result.isConfirmed) {
            fetch(`${VEHICLE_API}/${id}`, {method: 'DELETE'})
                    .then(response => {
                        if (response.ok) {
                            Swal.fire('Deleted!', 'Vehicle deleted.', 'success');
                            loadVehicles();
                        } else {
                            Swal.fire('Error!', 'Cannot delete assigned vehicle.', 'error');
                        }
                    });
        }
    });
}

// ✅ Close Modal
document.getElementById('cancelVehicle')?.addEventListener('click', () => {
    vehicleModal.style.display = 'none';
});
document.getElementById('closeVehicleModal')?.addEventListener('click', () => {
    vehicleModal.style.display = 'none';
});

// ✅ Load Vehicles on Page Load
if (document.getElementById('vehiclesTable'))
    loadVehicles();


// =========================
// Modal Close Buttons
// =========================
document.querySelectorAll('.close').forEach(btn => {
    btn.addEventListener('click', () => {
        btn.parentElement.parentElement.style.display = 'none';
    });
});
