package org.acme.controller;

import org.acme.repository.PizzaOrderRepository;
import org.acme.service.PizzaOrderService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/pizza-order")
public class PizzaOrderController {
    @Inject
    PizzaOrderRepository pizzaOrderRepository;

    @Inject
    PizzaOrderService pizzaOrderService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response putAnPizzaOrder(@QueryParam("type") String type, @QueryParam("size") String size) {
        String result = "Orden generada con el numero: " + pizzaOrderService.runWorkFlow(type, size);
        return Response.ok(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderStatus(@QueryParam("id") long id) {
        var response = pizzaOrderService.getOrderResponseById(id);
        return Response.ok(
                Map.of("id", response.getId(), "state", response.getState(), "description", response.getDescription()))
                .build();
    }
}
