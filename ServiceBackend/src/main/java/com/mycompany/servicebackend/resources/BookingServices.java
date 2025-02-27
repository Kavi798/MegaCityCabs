package com.mycompany.servicebackend.resources;

import Booking.Bookings;
import Booking.BookingOperations;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
        return Response.status(Response.Status.CREATED).entity("{\"message\": \"Booking created successfully\", \"id\": " + bookingId + "}").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings() {
        List<Bookings> bookings = BookingOperations.getAllBookings();
        return Response.ok(gson.toJson(bookings)).build();
    }
}
