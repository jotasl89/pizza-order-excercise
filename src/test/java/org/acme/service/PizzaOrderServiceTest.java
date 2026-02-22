package org.acme.service;

import org.acme.repository.PizzaOrderRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.acme.entity.PizzaOrder;
import org.acme.enums.OrderStatus;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class PizzaOrderServiceTest {

    @Mock
    PizzaOrderRepository pizzaOrderRepository;

    @InjectMocks
    PizzaOrderService pizzaOrderService;

    @Test
    void shouldCallPizzaOrderRepository() {
        PizzaOrder order = Mockito.mock(PizzaOrder.class);

        pizzaOrderService.saveOrder("Margarita", "Medium", 1);

        verify(pizzaOrderRepository, times(1)).persist(any(PizzaOrder.class));
    }

    @Test
    void shouldUpdateOrderStatus() {
        PizzaOrder order = Mockito.mock(PizzaOrder.class);

        when(pizzaOrderRepository.findById(1L)).thenReturn(order);

        pizzaOrderService.updateOrderStatus(1L, OrderStatus.PAYMENT_CONFIRMED, "Payment confirmed");

        verify(pizzaOrderRepository, times(1)).persist(any(PizzaOrder.class));
    }

    @Test
    void shouldReturnOrderResponse() {
        PizzaOrder order = Mockito.mock(PizzaOrder.class);
        @SuppressWarnings("unchecked")
        PanacheQuery<PizzaOrder> query = Mockito.mock(PanacheQuery.class);
        order.id = 1L;
        order.state = OrderStatus.CREATED;

        when(pizzaOrderRepository.find("orderNumber", 1L)).thenReturn(query);
        when(query.firstResult()).thenReturn(order);

        var response = pizzaOrderService.getOrderResponseById(1L);

        assert response.getId() == 1L;
        assert response.getState().equals(OrderStatus.CREATED.name());
    }
}
