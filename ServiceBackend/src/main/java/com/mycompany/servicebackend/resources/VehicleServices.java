package com.mycompany.servicebackend.resources;

import Vehicle.Vehicles;
import Vehicle.VehicleOperations;
import com.google.gson.Gson;
import com.google.gson.JsonObject; // ✅ Correct import for JsonObject
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("vehicles")
public class VehicleServices {

    private final Gson gson = new Gson();

    // ✅ Create Vehicle with Capacity Validation
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVehicle(String json) {
        Vehicles vehicle = gson.fromJson(json, Vehicles.class);

        // ✅ Validate Capacity (must be greater than 0)
        if (vehicle.getCapacity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Capacity must be greater than zero.\"}")
                    .build();
        }

        // ✅ Add Vehicle if valid
        int vehicleId = VehicleOperations.addVehicle(vehicle);
        if (vehicleId > 0) {
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Vehicle created successfully\", \"id\": " + vehicleId + "}")
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\": \"Failed to create vehicle\"}")
                .build();
    }

    // ✅ Get All Vehicles
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVehicles() {
        List<Vehicles> vehicles = VehicleOperations.getAllVehicles();
        return Response.ok(gson.toJson(vehicles)).build();
    }

    // ✅ Delete Vehicle
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVehicle(@PathParam("id") int id) {
        int deleted = VehicleOperations.deleteVehicle(id);
        if (deleted > 0) {
            return Response.ok("{\"message\": \"Vehicle deleted successfully\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\": \"Failed to delete vehicle\"}")
                .build();
    }

    // ✅ Get Available Vehicles by Type
    @GET
    @Path("/available/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableVehiclesByType(@PathParam("type") String type) {
        List<Vehicles> vehicles = VehicleOperations.getAvailableVehiclesByType(type);
        return Response.ok(gson.toJson(vehicles)).build();
    }

    // ✅ Get All Available Vehicles
    @GET
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableVehicles() {
        List<Vehicles> vehicles = VehicleOperations.getAvailableVehicles();
        return Response.ok(gson.toJson(vehicles)).build();
    }

    // Assign Both Vehicle and Driver to Booking
    @PUT
    @Path("/assign-vehicle-driver/{bookingId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignVehicleAndDriver(@PathParam("bookingId") int bookingId, String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        int vehicleId = jsonObject.get("vehicleId").getAsInt();
        int driverId = jsonObject.get("driverId").getAsInt();

        boolean success = VehicleOperations.assignVehicleAndDriverToBooking(vehicleId, driverId, bookingId);
        return success ? Response.ok("{\"message\": \"Vehicle and Driver assigned successfully, statuses updated.\"}").build()
                : Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Assignment failed.\"}").build();
    }

    // ✅ Update Vehicle
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVehicle(@PathParam("id") int id, String json) {
        Vehicles updatedVehicle = gson.fromJson(json, Vehicles.class); // Parse JSON into Vehicle object

        boolean success = VehicleOperations.updateVehicle(id, updatedVehicle); // Call operation layer
        if (success) {
            return Response.ok("{\"message\": \"Vehicle updated successfully.\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"Vehicle not found or update failed.\"}").build();
        }
    }

    // ✅ Get Vehicle By ID (for editing)
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVehicleById(@PathParam("id") int id) {
        Vehicles vehicle = VehicleOperations.getVehicleById(id); // You need to implement this method in VehicleOperations
        if (vehicle != null) {
            return Response.ok(new Gson().toJson(vehicle)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Vehicle not found\"}")
                    .build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchVehicles(@QueryParam("type") String type, @QueryParam("status") String status) {
        List<Vehicles> vehicles = VehicleOperations.searchVehicles(
                (type != null && !type.isEmpty()) ? type : null,
                (status != null && !status.isEmpty()) ? status : null,
                null // Plate number search optional (currently null)
        );
        return Response.ok(gson.toJson(vehicles)).build();
    }
}
