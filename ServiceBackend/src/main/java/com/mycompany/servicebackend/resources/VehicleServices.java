package com.mycompany.servicebackend.resources;

import Vehicle.Vehicles;
import Vehicle.VehicleOperations;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("vehicles")
public class VehicleServices {

    private final Gson gson = new Gson();

    // Create Vehicle
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVehicle(String json) {
        Vehicles vehicle = gson.fromJson(json, Vehicles.class);
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

    // Get All Vehicles
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVehicles() {
        List<Vehicles> vehicles = VehicleOperations.getAllVehicles();
        return Response.ok(gson.toJson(vehicles)).build();
    }

    // Delete Vehicle
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
}
