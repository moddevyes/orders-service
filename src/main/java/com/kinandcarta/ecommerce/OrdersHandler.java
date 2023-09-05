package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.contracts.OrdersUseCases;
import com.kinandcarta.ecommerce.contracts.ServiceHandler;
import com.kinandcarta.ecommerce.entities.OrderLineItems;
import com.kinandcarta.ecommerce.entities.Orders;
import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.entities.OrdersAddress;
import com.kinandcarta.ecommerce.exceptions.InvalidAccountException;
import com.kinandcarta.ecommerce.exceptions.MissingAccountException;
import com.kinandcarta.ecommerce.exceptions.MissingAddressException;
import com.kinandcarta.ecommerce.exceptions.OrderModelNotPersistedException;
import com.kinandcarta.ecommerce.infrastructure.OrderLineItemsRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersAccountRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersAddressRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class OrdersHandler implements ServiceHandler, OrdersUseCases {
    final
    OrdersRepository ordersRepository;
    final
    OrderLineItemsRepository orderLineItemsRepository;

    final OrdersAccountRepository ordersAccountRepository;
    final OrdersAddressRepository ordersAddressRepository;

    final Map<String, String> errors;

    public OrdersHandler(OrdersRepository ordersRepository,
                         OrderLineItemsRepository orderLineItemsRepository,
                         OrdersAccountRepository ordersAccountRepository,
                         OrdersAddressRepository ordersAddressRepository) {
        this.ordersAccountRepository = ordersAccountRepository;
        this.ordersAddressRepository = ordersAddressRepository;
        errors = new HashMap<>();
        this.ordersRepository = ordersRepository;
        this.orderLineItemsRepository = orderLineItemsRepository;
    }

    @Override
    @Transactional
    public Orders create(final Orders model) {
        log.debug("create: model ->");
        validateOrdersAccount(model);

        validateOrdersAddress(model);

        try {
            saveTransientModels(model);
        } catch (final OrderModelNotPersistedException e) {
            throw new OrderModelNotPersistedException(e.toString());
        }

        return ordersRepository.save(model);
    }

    private void saveTransientModels(final Orders model) throws OrderModelNotPersistedException {
        // Orders Account
        try {

            Objects.requireNonNull(model.getOrdersAccount().getAddresses().stream().toList(), "Account requires at least one Address to create an Order.");
            Objects.requireNonNull(model.getOrderLineItems(), "Orders requires at lease one order line item.");

            OrdersAccount persistedAccount = ordersAccountRepository.save(model.getOrdersAccount());
            OrdersAddress persistedAddress = ordersAddressRepository.save(model.getOrdersShippingAddress());
            Set<OrderLineItems> orderLineItems = model.getOrderLineItems();

            Orders modelToPersist = Orders.builder()
                    .orderNumber(model.getOrderNumber())
                    .orderDate(Instant.now())
                    .ordersShippingAddress(persistedAddress)
                    .ordersAccount(persistedAccount)
                    .orderLineItems(orderLineItems)
                    .totalPrice(model.sumLineItems(orderLineItems))
                    .build();

            ordersRepository.save(modelToPersist);

        } catch (Exception e) {
            throw new OrderModelNotPersistedException("Orders persist FAILED.");
        }
    }

    private void validateOrdersAccount(final Orders modelToValidate) {
        // VERIFY__account_required
        assertOrderHasAccount(modelToValidate);

        OrdersAccount accountToRetrieveVerify = modelToValidate.getOrdersAccount();

        // VERIFY__first_lastname_required
        assertOrderAccountHasFirstandLastName(accountToRetrieveVerify);

        // VERIFY__email_address_required
        assertOrderAccountHasEmail(accountToRetrieveVerify);
    }

    private void validateOrdersAddress(final Orders modelToValidate) {
        // VERIFY__address_required
        assertOrderHasAddress(modelToValidate);
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


    private static void assertOrderAccountHasEmail(OrdersAccount accountToRetrieveVerify) {
        if (StringUtils.isEmpty(accountToRetrieveVerify.getEmailAddress())) {
            throw new InvalidAccountException("InvalidAccountException: [valid E-mail Address] required to create an Order.");
        }
    }

    private static void assertOrderAccountHasFirstandLastName(OrdersAccount accountToRetrieveVerify) {
        if (StringUtils.isEmpty(accountToRetrieveVerify.getFirstName()) &&
                StringUtils.isEmpty(accountToRetrieveVerify.getLastName()))  {
            throw new InvalidAccountException("InvalidAccountException: [valid Account with First and Last name] required to create an Order.");
        }
    }

    private static void assertOrderHasAccount(Orders modelToValidate) {
        if (modelToValidate.getOrdersAccount() == null) {
            throw new MissingAccountException("MissingAccountException: [valid Account] required to create an Order.");
        }
    }

    private static void assertOrderHasAddress(final Orders modelToValidate) {
        if (modelToValidate.getOrdersAccount().getAddresses() == null || modelToValidate.getOrdersShippingAddress() == null) {
            throw new MissingAddressException("MissingAddressException: [valid Account -> Address or Shipping Address] required to create an Order.");
        }
    }
}
