package com.mycompany.servicebackend.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.JsonObject;
import Booking.BookingOperations;
import Driver.DriverOperations;
import Vehicle.VehicleOperations;
import User.UserOperations;
import User.Users;
import java.util.List;
import java.util.stream.Collectors;

@Path("/dashboard") // Base path for this resource
public class DashboardServices {

    @GET
    @Path("/stats") // Sub-path for this method
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardStats() {
        try {
            // Fetch data from the database
            int totalBookings = BookingOperations.getAllBookings().size();
            int availableDrivers = DriverOperations.getAvailableDrivers().size();
            int availableVehicles = VehicleOperations.getAvailableVehicles().size();
            // Fetch all users and filter customers
            List<Users> allUsers = UserOperations.getAllAccounts();
            System.out.println("Total Users: " + allUsers.size());

            List<Users> customers = allUsers.stream()
                    .filter(user -> "customer".equalsIgnoreCase(user.getRole()))
                    .collect(Collectors.toList());
            System.out.println("Total Customers: " + customers.size());

            int totalCustomers = customers.size();

            // Create a JSON response
            JsonObject stats = new JsonObject();
            stats.addProperty("totalBookings", totalBookings);
            stats.addProperty("availableDrivers", availableDrivers);
            stats.addProperty("availableVehicles", availableVehicles);
            stats.addProperty("totalCustomers", totalCustomers);

            return Response.ok(stats.toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to fetch dashboard stats\"}")
                    .build();
        }
    }
}