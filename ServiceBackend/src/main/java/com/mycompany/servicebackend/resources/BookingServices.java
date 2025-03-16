package com.mycompany.servicebackend.resources;

import Booking.Bookings;
import Booking.BookingOperations;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("bookings")
public class BookingServices {

    private final Gson gson = new Gson();

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(String json) {
        Bookings booking = gson.fromJson(json, Bookings.class);
        int bookingId = BookingOperations.createBooking(booking);

        if (bookingId == -1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"No available drivers or vehicles.\"}").build();
        }

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"Booking created successfully\", \"id\": " + bookingId + "}").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings() {
        List<Bookings> bookings = BookingOperations.getAllBookings();
        return Response.ok(gson.toJson(bookings)).build();
    }
    // ✅ Bill Generation API

    @GET
    @Path("/generate-bill/{bookingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateBill(@PathParam("bookingId") int bookingId) {
        Bookings booking = BookingOperations.getBookingDetailsWithCustomer(bookingId);
        if (booking != null) {
            // Directly use booking.getFare() instead of calculated fare
            String billJson = gson.toJson(new Bill(booking, booking.getFare()));
            return Response.ok(billJson).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"Booking not found\"}").build();
        }
    }

    // ✅ Fare Calculation
    private double calculateFare(Bookings booking) {
        double baseFare = 200.00;
        double distanceKm = 5.0; // You can update this to dynamic value if distance added in DB
        double perKmRate = 50.00;
        return baseFare + (distanceKm * perKmRate);
    }

    // ✅ Helper Class for Bill Response
    private class Bill {

        private final String customerName;
        private final String customerAddress;
        private final String customerPhone;
        private final String pickupLocation;
        private final String dropoffLocation;
        private final double totalFare;

        public Bill(Bookings booking, double totalFare) {
            this.customerName = booking.getCustomerName();
            this.customerAddress = booking.getCustomerAddress();
            this.customerPhone = booking.getCustomerPhone();
            this.pickupLocation = booking.getPickupLocation();
            this.dropoffLocation = booking.getDropoffLocation();
            this.totalFare = totalFare;
        }
    }
    // ✅ Update Booking Status API

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBookingStatus(@PathParam("id") int id, String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            String status = jsonObject.get("status").getAsString();

            boolean updated = BookingOperations.updateBookingStatus(id, status);
            if (updated) {
                return Response.ok("{\"message\": \"Booking status updated successfully\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"Booking not found or update failed\"}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\": \"Invalid request format\"}").build();
        }
    }

    // ✅ Assign Vehicle to Booking with Status Check
    @PUT
    @Path("/assign-vehicle/{bookingId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignVehicleToBooking(@PathParam("bookingId") int bookingId, String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            int vehicleId = jsonObject.get("vehicleId").getAsInt();

            boolean success = BookingOperations.assignVehicleToBooking(bookingId, vehicleId);
            if (success) {
                return Response.ok("{\"message\": \"Vehicle assigned successfully!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\": \"Cannot assign vehicle to this booking (invalid status or not found).\"}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\": \"Invalid request format!\"}").build();
        }
    }

    @GET
    @Path("/history/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingsByUser(@PathParam("userId") int userId) {
        List<Bookings> bookings = BookingOperations.getBookingsByUser(userId);
        return Response.ok(gson.toJson(bookings)).build();
    }

    @PUT
    @Path("/cancel/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@PathParam("id") int id) {
        boolean cancelled = BookingOperations.cancelBooking(id);
        if (cancelled) {
            return Response.ok("{\"message\": \"Booking cancelled successfully\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Booking not found or cannot be cancelled\"}").build();
        }
    }
}
