package org.example.user;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.common.PageRequest;
import org.example.dto.UserDto;

@Path("/users")
public class UserController {

    @Inject
    private UserService userService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(UserDto dto) {
        return Response.status(Response.Status.CREATED)
                .entity(userService.add(dto))
                .build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(UserDto dto) {
        return Response.status(Response.Status.OK)
                .entity(userService.add(dto))
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") Long id) {
        return Response.ok().entity(userService.get(id)).build();
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(PageRequest pageRequest) {
        return Response.ok().entity(userService.getAllUsers(pageRequest)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/activate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response activateAccount(@QueryParam("token") String token) {
        try {
            userService.activateAccount(token);
            return Response.ok("Account activated successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid activation token").build();
        }
    }
}
