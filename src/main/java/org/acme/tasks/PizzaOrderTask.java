package org.acme.tasks;

import org.acme.dto.PizzaOrderResponse;
import org.acme.enums.OrderStatus;
import org.acme.service.PizzaOrderService;
import org.jboss.logging.Logger;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;
import jakarta.inject.Inject;

@LHTask
public class PizzaOrderTask {

    public static final String CREATE_ORDER_TASK = "create-order";
    public static final String PAYMENT_CONFIRM_TASK = "payment-confirm";
    public static final String PREPARE_PIZZA_TASK = "prepare-pizza";
    public static final String BAKE_PIZZA_TASK = "bake-pizza";
    public static final String DELIVER_PIZZA_TASK = "deliver-pizza";
    public static final String COMPLETE_ORDER_TASK = "complete-order";
    public static final String CANCEL_ORDER_TASK = "cancel-order";

    @Inject
    private PizzaOrderService pizzaOrderService;

    @LHTaskMethod(CREATE_ORDER_TASK)
    public PizzaOrderResponse createOrder(String pizzaType, String pizzaSize, WorkerContext context) {
        Integer orderNumber = Integer.parseInt(context.getWfRunId().getId());
        PizzaOrderResponse response = pizzaOrderService.saveOrder(pizzaType, pizzaSize, orderNumber);
        return response;
    }

    @LHTaskMethod(PAYMENT_CONFIRM_TASK)
    public PizzaOrderResponse confirmPayment(Long orderId) throws LHTaskException {
        // Simular proceso de pago con delay
        try {
            Thread.sleep(20000); // 20 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new LHTaskException("Payment failed", "Payment failed"); // Simular fallo de pago
        // return pizzaOrderService.updateOrderStatus(orderId,
        // OrderStatus.PAYMENT_CONFIRMED, "Payment confirmed");
    }

    @LHTaskMethod(PREPARE_PIZZA_TASK)
    public PizzaOrderResponse preparePizza(Long orderId) throws LHTaskException {
        // Simular proceso de preparación con delay
        try {
            Thread.sleep(15000); // 15 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return pizzaOrderService.updateOrderStatus(orderId, OrderStatus.PREPARING, "Preparing pizza");
    }

    @LHTaskMethod(BAKE_PIZZA_TASK)
    public PizzaOrderResponse bakePizza(Long orderId) throws LHTaskException {
        // Simular proceso de horneado con delay
        try {
            Thread.sleep(20000); // 20 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return pizzaOrderService.updateOrderStatus(orderId, OrderStatus.BAKED, "Pizza baked");
    }

    @LHTaskMethod(DELIVER_PIZZA_TASK)
    public PizzaOrderResponse deliverPizza(Long orderId) throws LHTaskException {
        // Simular proceso de entrega con delay
        try {
            Thread.sleep(30000); // 30 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return pizzaOrderService.updateOrderStatus(orderId, OrderStatus.DELIVERED, "Pizza delivered");
    }

    @LHTaskMethod(COMPLETE_ORDER_TASK)
    public PizzaOrderResponse completeOrder(Long orderId) throws LHTaskException {
        return pizzaOrderService.updateOrderStatus(orderId, OrderStatus.COMPLETED, "Order completed");
    }

    @LHTaskMethod(CANCEL_ORDER_TASK)
    public PizzaOrderResponse cancelOrder(Long orderId, String reason) throws LHTaskException {
        return pizzaOrderService.updateOrderStatus(orderId, OrderStatus.CANCELLED, reason);
    }
}
