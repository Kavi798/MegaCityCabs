package com.mycompany.servicebackend.resources;

import User.Users;
import User.UserOperations;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("users")
public class UserServices {
    private final Gson gson = new Gson();

    // ✅ Register User API
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String json) {
        try {
            Users user = gson.fromJson(json, Users.class);
            int userId = UserOperations.addAccount(user);
            
            if (userId > 0) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"message\": \"User created successfully\", \"id\": " + userId + "}")
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Failed to create user\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Invalid request format\"}")
                    .build();
        }
    }

    // ✅ Get All Users
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<Users> users = UserOperations.getAllAccounts();
        return Response.ok(gson.toJson(users)).build();
    }

    // ✅ Delete User
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") int id) {
        int deleted = UserOperations.deleteAccount(id);
        if (deleted > 0) {
            return Response.ok("{\"message\": \"User deleted successfully\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\": \"Failed to delete user\"}")
                .build();
    }

    // ✅ User Login API
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateLogin(String json) {
        try {
            Users user = gson.fromJson(json, Users.class);
            if (user.getEmail() == null || user.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Email and Password are required!\"}")
                        .build();
            }

            Users validUser = UserOperations.validateLogin(user.getEmail(), user.getPassword());

            if (validUser != null) {
                return Response.ok("{\"message\": \"Login successful!\", \"id\": " + validUser.getId() + ", \"role\": \"" + validUser.getRole() + "\"}").build();
            }

            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Invalid email or password!\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Invalid request format\"}")
                    .build();
        }
    }

    // ✅ Test Endpoint
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testEndpoint() {
        return Response.ok("Test endpoint is working!").build();
    }
}
