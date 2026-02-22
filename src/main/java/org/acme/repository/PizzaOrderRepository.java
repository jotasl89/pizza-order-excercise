package org.acme.repository;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.entity.PizzaOrder;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PizzaOrderRepository implements PanacheRepository<PizzaOrder> {
    // Métodos personalizados si se requieren
}
