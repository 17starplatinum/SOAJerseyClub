package ru.itmo.cs.dandadan.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.ErrorResponse;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.service.api.HumanBeingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Path("/human-beings")
public class HumanBeingResource {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    @Inject
    private HumanBeingService humanBeingService;

    @GET
    public Response getHumanBeings(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();

        List<String> sortParameters = qp.get("sort");
        List<String> filterParameters = qp.get("filter");

        String pageParam = qp.getFirst("page");
        String pageSizeParam = qp.getFirst("pageSize");
        int page;
        int pageSize;
        try {
            page = parsePositiveIntOrDefault(pageParam, DEFAULT_PAGE);
            if (page < 1) {
                throw new CustomBadRequestException("page", "positive integer", Integer.toString(page));
            }
            pageSize = parsePositiveIntOrDefault(pageSizeParam, DEFAULT_PAGE_SIZE);
            if (pageSize < 1) {
                throw new CustomBadRequestException("pageSize", "positive integer", Integer.toString(pageSize));
            }
        } catch (CustomBadRequestException e) {
            return Response.status(400).entity(new ErrorResponse(400, e.getMessage())).build();
        }
        List<String> sort = sortParameters == null ? new ArrayList<>() :
                sortParameters.stream()
                        .filter(Objects::nonNull)
                        .filter(Predicate.not(String::isBlank))
                        .collect(Collectors.toList());
        List<String> filter = filterParameters == null ? new ArrayList<>() :
                filterParameters.stream()
                        .filter(Objects::nonNull)
                        .filter(Predicate.not(String::isBlank))
                        .collect(Collectors.toList());
        Page<HumanBeingResponse> humanBeings = humanBeingService.getHumanBeings(sort, filter, page, pageSize);
        return Response.ok(humanBeings, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") long id) {
        return Response.ok(humanBeingService.getHumanBeing(id), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/unique-speeds")
    public Response getUniqueSpeeds() {
        return Response.ok(humanBeingService.getUniqueImpactSpeeds(), MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addHumanBeing(HumanBeingRequest humanBeingRequest) {
        return Response.ok(humanBeingService.addHumanBeing(humanBeingRequest), MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateHumanBeing(@PathParam("id") long id, HumanBeingRequest humanBeingRequest) {
        return Response.ok(humanBeingService.updateHumanBeing(id, humanBeingRequest), MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteHumanBeing(@PathParam("id") long id) {
        humanBeingService.deleteHumanBeing(id);
        return Response.noContent().build();
    }

    private int parsePositiveIntOrDefault(String s, int defaultValue) {
        if (s == null || s.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(s);
    }
}
