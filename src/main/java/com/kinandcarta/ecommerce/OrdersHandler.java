package com.kinandcarta.ecommerce;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class OrdersHandler implements ServiceHandler, OrdersUseCases {
    final
    OrdersRepository ordersRepository;
    final
    OrderLineItemsRepository orderLineItemsRepository;

    public OrdersHandler(OrdersRepository ordersRepository, OrderLineItemsRepository orderLineItemsRepository) {
        this.ordersRepository = ordersRepository;
        this.orderLineItemsRepository = orderLineItemsRepository;
    }

    @Override
    @Transactional
    public Orders create(Orders model) {
        log.debug("create: model ->" + model.toString());
        return ordersRepository.save(model);
    }

    @Override
    @Transactional
    public Orders update(final Long id, Orders model) {
        log.debug("update: id -> " + id + ", model -> " + model.toString());

        BigDecimal defaPrice = BigDecimal.ZERO;

        Orders orderExisting = findById(id);

        // Required Fields: IF any are not there, this is an invalid Order ...
        orderExisting.setOrdersAccount(Objects.requireNonNullElse(model.getOrdersAccount(), orderExisting.getOrdersAccount()));
        orderExisting.setOrderNumber(Objects.requireNonNullElse(model.getOrderNumber(), orderExisting.getOrderNumber()));
        orderExisting.setOrderDate(Objects.requireNonNullElse(model.getOrderDate(), orderExisting.getOrderDate()));

        // Optional Fields (assign default values if existing field and input field is null, okay):
        orderExisting.setOrdersShippingAddress(
                Objects.requireNonNullElse(model.getOrdersShippingAddress(),
                        Objects.requireNonNullElse(orderExisting.getOrdersShippingAddress(), new OrdersAddress())));

        orderExisting.setTotalPrice(
                Objects.requireNonNullElse(model.getTotalPrice(),
                        Objects.requireNonNullElse(orderExisting.getTotalPrice(), defaPrice)));

        // VERIFY Order Line Items - current and passed in for update

        // A null AND B null          .: empty HashSet<>()
        if (model.getOrderLineItems() == null && orderExisting.getOrderLineItems() == null)  {
            orderExisting.setOrderLineItems(new HashSet<>());
        }

        // B has order items [AND] A has order items   .: MERGE B into A (doesn't lose anything)
        if ( (model.getOrderLineItems() != null && !model.getOrderLineItems().isEmpty()) && (orderExisting.getOrderLineItems() != null && !orderExisting.getOrderLineItems().isEmpty())) {
            orderExisting.setOrderLineItems(
                    model.getOrderLineItems()
            );
        }

        // B null AND A has order items .: nothing to do

        // B has order items A null     .: add B into A
        if ((model.getOrderLineItems() != null && !model.getOrderLineItems().isEmpty()) && (orderExisting.getOrderLineItems() == null)) {
            orderExisting.setOrderLineItems(model.getOrderLineItems());
        }

        // What are the rules for update?
        return ordersRepository.save(orderExisting);
    }

    @Override
    public void delete(final Long id) {
        log.debug("delete: id -> " + id);
        ordersRepository.deleteById(id);
    }

    @Override
    public Orders findById(final Long id) {
        log.debug("findById: id -> " + id);
        if (!ordersRepository.existsById(id)) {
            throw new EntityNotFoundException("findById failed for id ->" + id);
        }
        return ordersRepository.getReferenceById(id);
    }

    @Override
    public Set<Orders> findAll() {
        log.debug("findAll");
        return new HashSet<>(ordersRepository.findAll());
    }

    @Override
    public Set<OrderLineItems> findOrderLineItemsFor(Long orderId) {
        Orders ordersWithLineItems = findById(orderId);
        Objects.requireNonNull(ordersWithLineItems, "Order was null for METHOD: findOrderLineItemsFor");
        Objects.requireNonNull(ordersWithLineItems.getOrderLineItems(), "METHOD: findOrderLineItemsFor, Found Order, but not Order Line Items for ID -> " + orderId);
        return new HashSet<>(!ordersWithLineItems.getOrderLineItems().isEmpty() ?
                ordersWithLineItems.getOrderLineItems() : new HashSet<>());
    }


}
