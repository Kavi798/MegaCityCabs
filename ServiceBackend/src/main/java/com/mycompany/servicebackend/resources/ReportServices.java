package com.mycompany.servicebackend.resources;

import Reports.ReportsOperations;
import com.google.gson.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("reports")
public class ReportServices {

    // Total Bookings API
    @GET
    @Path("/total-bookings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTotalBookings() {
        int totalBookings = ReportsOperations.getTotalBookings();
        JsonObject json = new JsonObject();
        json.addProperty("totalBookings", totalBookings);
        return Response.ok(json.toString()).build();
    }

    // Total Revenue API
    @GET
    @Path("/total-revenue")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTotalRevenue() {
        double totalRevenue = ReportsOperations.getTotalRevenue();
        JsonObject json = new JsonObject();
        json.addProperty("totalRevenue", totalRevenue);
        return Response.ok(json.toString()).build();
    }
}
