package com.mycompany.servicebackend.resources;

import Driver.Drivers;
import Driver.DriverOperations;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("drivers")
public class DriverServices {

    private final Gson gson = new Gson();

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDriver(String json) {
        try {
            System.out.println("Received JSON: " + json); // ✅ Check raw JSON

            Drivers driver = gson.fromJson(json, Drivers.class);
            System.out.println("Parsed Driver Object: " + driver.getdName() + ", " + driver.getLicenseNumber()); // ✅ Debug parsed object

            int driverId = DriverOperations.addDriver(driver);
            if (driverId > 0) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"message\": \"Driver created successfully\", \"id\": " + driverId + "}")
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Failed to create driver\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Full stack trace
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Invalid request format: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ✅ Get All Drivers
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDrivers() {
        List<Drivers> drivers = DriverOperations.getAllDrivers();
        return Response.ok(gson.toJson(drivers)).build();
    }

    // ✅ Delete Driver
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDriver(@PathParam("id") int id) {
        int deleted = DriverOperations.deleteDriver(id);
        if (deleted > 0) {
            return Response.ok("{\"message\": \"Driver deleted successfully\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\": \"Failed to delete driver\"}")
                .build();
    }

    // ✅ Assign Driver to Booking
    @PUT
    @Path("/{driverId}/assignBooking/{bookingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignDriverToBooking(@PathParam("driverId") int driverId,
            @PathParam("bookingId") int bookingId,
            @QueryParam("date") String requestedDate) {
        if (requestedDate == null || requestedDate.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Booking date is required!\"}")
                    .build();
        }

        boolean success = DriverOperations.assignDriverToBooking(driverId, bookingId, requestedDate);
        if (success) {
            return Response.ok("{\"message\": \"Driver assigned successfully\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\": \"Failed to assign driver\"}")
                .build();
    }

    // ❌ Optional: Get Assigned Vehicle for Driver (Commented out if no vehicles)
    /*
    @GET
    @Path("/{driverId}/vehicle")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDriverVehicle(@PathParam("driverId") int driverId) {
        String vehicleDetails = DriverOperations.getDriverVehicleDetails(driverId);
        return Response.ok(vehicleDetails).build();
    }
     */
    // ✅ Get All Available Drivers
    @GET
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableDrivers() {
        List<Drivers> availableDrivers = DriverOperations.getAvailableDrivers();
        return Response.ok(gson.toJson(availableDrivers)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDriverById(@PathParam("id") int id) {
        Drivers driver = DriverOperations.getDriverById(id);
        if (driver != null) {
            return Response.ok(new Gson().toJson(driver)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Driver not found\"}")
                    .build();
        }
    }

    // ✅ Update Driver
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDriver(@PathParam("id") int id, String json) {
        try {
            Drivers updatedDriver = gson.fromJson(json, Drivers.class);
            updatedDriver.setId(id); // Ensure the ID is set correctly
            boolean updated = DriverOperations.updateDriver(updatedDriver);
            if (updated) {
                return Response.ok("{\"message\": \"Driver updated successfully\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Driver not found or update failed\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Debugging
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Invalid request format: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    

}
