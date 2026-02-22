package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.repository.PizzaOrderRepository;

import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.sdk.common.proto.WorkflowEventDefId;
import io.smallrye.mutiny.Uni;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.AwaitWorkflowEventRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

import org.acme.dto.PizzaOrderResponse;
import org.acme.entity.PizzaOrder;
import org.acme.enums.OrderStatus;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PizzaOrderService {

    @Inject
    PizzaOrderRepository pizzaOrderRepository;

    private final LittleHorseBlockingStub littleHorseBlockingClient;

    PizzaOrderService(LittleHorseBlockingStub littleHorseBlockingClient, PizzaOrderRepository pizzaOrderRepository) {
        this.littleHorseBlockingClient = littleHorseBlockingClient;
        this.pizzaOrderRepository = pizzaOrderRepository;
    }

    @Transactional
    public PizzaOrderResponse saveOrder(String type, String size, Integer orderNumber) {
        PizzaOrder order = new PizzaOrder();
        order.state = OrderStatus.CREATED;
        order.pizzaType = type;
        order.size = size;
        order.orderNumber = orderNumber;
        pizzaOrderRepository.persist(order);
        return PizzaOrderResponse.builder()
                .id(order.id)
                .state(order.state.name())
                .orderNumber(order.orderNumber)
                .build();
    }

    @Transactional
    public PizzaOrderResponse updateOrderStatus(Long id, OrderStatus orderStatus, String description) {
        PizzaOrder order = pizzaOrderRepository.findById(id);
        order.state = orderStatus;
        order.description = description;
        pizzaOrderRepository.persist(order);
        return PizzaOrderResponse.builder()
                .id(order.id)
                .state(order.state.name())
                .description(order.description)
                .build();
    }

    public String runWorkFlow(String type, String size) {
        Integer orderNumber = new Random().nextInt(1000);
        WfRun result = littleHorseBlockingClient.runWf(
                RunWfRequest.newBuilder()
                        .setId(orderNumber.toString())
                        .setWfSpecName("pizza-order-wf")
                        .putVariables("pizzaType", LHLibUtil.objToVarVal(type))
                        .putVariables("pizzaSize", LHLibUtil.objToVarVal(size))
                        .build());
        return result.getId().getId();
    }

    public PizzaOrderResponse getOrderResponseById(long id) {
        PizzaOrder order = pizzaOrderRepository.find("orderNumber", id).firstResult();
        return PizzaOrderResponse.builder()
                .id(order.id)
                .state(order.state.name())
                .description(order.description)
                .build();
    }

}
