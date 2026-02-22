package org.acme.workflows;

import org.acme.tasks.PizzaOrderTask;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow("pizza-order-wf")
public class PizzaOrderWorkflow implements LHWorkflowDefinition {

    public static final String ORDER_WORKFLOW = "order-workflow";
    public static final String PIZZA_TYPE_VARIABLE = "pizzaType";
    public static final String PIZZA_SIZE_VARIABLE = "pizzaSize";
    public static final String ORDER_ID_VARIABLE = "id";
    public static final String REASON_VARIABLE = "reason";

    @Override
    public void define(WorkflowThread wf) {
        var pizzaType = wf.declareStr(PIZZA_TYPE_VARIABLE).required();
        var pizzaSize = wf.declareStr(PIZZA_SIZE_VARIABLE).required();
        var orderId = wf.declareInt(ORDER_ID_VARIABLE).withDefault(-1);
        var reason = wf.declareStr(REASON_VARIABLE).withDefault("No reason provided");

        var orderResponse = wf.execute(PizzaOrderTask.CREATE_ORDER_TASK, pizzaType, pizzaSize);
        orderId.assign(orderResponse.jsonPath("$.id"));

        var paymentResponse = wf.execute(PizzaOrderTask.PAYMENT_CONFIRM_TASK, orderId);
        wf.handleException(paymentResponse, handler -> {
            reason.assign("Payment failed");
            var cancelResponse = handler.execute(PizzaOrderTask.CANCEL_ORDER_TASK, orderId, reason);
            handler.throwEvent(ORDER_WORKFLOW, cancelResponse);
            handler.fail(cancelResponse, "payment-failed", "Workflow failed due to payment failure");
        });

        var prepareResponse = wf.execute(PizzaOrderTask.PREPARE_PIZZA_TASK, orderId);
        wf.handleException(prepareResponse, handler -> {
            reason.assign("Preparation failed");
            var cancelResponse = handler.execute(PizzaOrderTask.CANCEL_ORDER_TASK, orderId, reason);
            handler.throwEvent(ORDER_WORKFLOW, cancelResponse);
            handler.fail(cancelResponse, "preparation-failed", "Workflow failed due to preparation failure");
        });

        var bakeResponse = wf.execute(PizzaOrderTask.BAKE_PIZZA_TASK, orderId);
        wf.handleException(bakeResponse, handler -> {
            reason.assign("Baking failed");
            var cancelResponse = handler.execute(PizzaOrderTask.CANCEL_ORDER_TASK, orderId, reason);
            handler.throwEvent(ORDER_WORKFLOW, cancelResponse);
            handler.fail(cancelResponse, "baking-failed", "Workflow failed due to baking failure");
        });

        var deliverResponse = wf.execute(PizzaOrderTask.DELIVER_PIZZA_TASK, orderId);
        wf.handleException(deliverResponse, handler -> {
            reason.assign("Delivery failed");
            var cancelResponse = handler.execute(PizzaOrderTask.CANCEL_ORDER_TASK, orderId, reason);
            handler.throwEvent(ORDER_WORKFLOW, cancelResponse);
            handler.fail(cancelResponse, "delivery-failed", "Workflow failed due to delivery failure");
        });

        wf.execute(PizzaOrderTask.COMPLETE_ORDER_TASK, orderId);

    }

}
