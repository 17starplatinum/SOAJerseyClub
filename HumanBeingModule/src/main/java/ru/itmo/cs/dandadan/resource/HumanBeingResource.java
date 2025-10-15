package ru.itmo.cs.dandadan.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.service.api.HumanBeingService;

@Slf4j
@Path("/api/v1/human-beings")
public class HumanBeingResource {
    @Inject
    private HumanBeingService humanBeingService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHumanBeings() {

    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") long id) {

    }

    @GET
    @Path("unique-speeds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUniqueSpeeds() {

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHumanBeing(HumanBeingRequest humanBeingRequest) {

    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHumanBeing(@PathParam("id") long id, HumanBeingRequest humanBeingRequest) {

    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHumanBeing(@PathParam("id") long id) {

    }
}
