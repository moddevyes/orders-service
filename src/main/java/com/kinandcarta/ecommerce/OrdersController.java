package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.clients.AccountServiceClient;
import com.kinandcarta.ecommerce.contracts.ControllerOrdersUseCases;
import com.kinandcarta.ecommerce.contracts.CrudUseCase;
import com.kinandcarta.ecommerce.entities.AccountOrderDetails;
import com.kinandcarta.ecommerce.entities.OrderLineItems;
import com.kinandcarta.ecommerce.entities.Orders;
import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.exceptions.*;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@Slf4j
public class OrdersController implements CrudUseCase<Orders>, ControllerOrdersUseCases {
    final AccountServiceClient accountServiceClient;
    final OrdersHandler ordersHandler;

    public OrdersController(OrdersHandler ordersHandler, AccountServiceClient accountServiceClient) {
        this.ordersHandler = ordersHandler;
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    @PostMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Orders> create(@RequestBody final Orders model) {
        try {
            if (model == null) return ResponseEntity.badRequest().build();

            assertOrderHasAccount(model);

            OrdersAccount ordersAccount = accountServiceClient.findByAccountIdRef(model.getOrdersAccount().getAccountRefId());
            if (null == ordersAccount) throw new AccountNotFoundException("Account not found for ID -> " + model.getOrdersAccount().getAccountRefId());

            return new ResponseEntity<>(ordersHandler.create(model), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, create, exception(s) occurred." + e);

            if (e instanceof InvalidAccountException ||
                    e instanceof MissingAccountException ||
                    e instanceof MissingAddressException ||
                    e instanceof OrderModelNotPersistedException ||
                    e instanceof DataIntegrityViolationException ||  e instanceof NullPointerException)
            { return ResponseEntity.badRequest().build(); }

            return ResponseEntity.notFound().build();

        }
    }
    private static void assertOrderHasAccount(Orders modelToValidate) {
        if (modelToValidate.getOrdersAccount() == null) {
            throw new MissingAccountException("MissingAccountException: [valid Account] required to create an Order.");
        }
        if (modelToValidate.getOrdersAccount().getAccountRefId() == null) {
            throw new MissingAccountException("MissingAccountException: [valid Account ID_REF required to create an Order.");
        }
    }
    @Override
    @PutMapping(value = "/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Orders> update(@PathVariable("id") @NotNull final Long id, @RequestBody Orders model) {
        try {
            if (id == null) return ResponseEntity.badRequest().build();

            return new ResponseEntity<>(ordersHandler.update(id, model), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, update, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable("id") @NotNull final Long id) {
        try {
            if (id == null) return;

            ordersHandler.delete(id);
        } catch (final Exception e) {
            log.error("::METHOD, delete, exception occurred.", e);
        }
    }

    @Override
    @GetMapping(value = "/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Orders> findById(@PathVariable("id") @NotNull final Long id) {
        try {
            if (id == null) return ResponseEntity.badRequest().build();

            return new ResponseEntity<>(ordersHandler.findById(id), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findById, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping(value = "/orders/{id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountOrderDetails> findByIdDetailedView(@PathVariable("id") @NotNull final Long id) {
        try {
            if (id == null) return ResponseEntity.badRequest().build();

            return new ResponseEntity<>(ordersHandler.findByIdDetailedView(id), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findById, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Orders>> findAll(
            @RequestParam(value = "accountId", required = false) @NotNull final Long accountId) {
        try {
            if (accountId == null) {
                return new ResponseEntity<>(
                        Optional.ofNullable(ordersHandler.findAll()).orElse(new HashSet<>()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ordersHandler.findOrdersForAccountId(accountId), HttpStatus.OK);
            }
        } catch (final Exception e) {
            log.error("::METHOD, findAll, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping(value = "/orders/{id}/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<OrderLineItems>> findOrderLineItemsFor(@PathVariable("id") @NotNull final Long id) {
        try {
            return new ResponseEntity<>(ordersHandler.findOrderLineItemsFor(id), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findOrderLineItemsFor, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }

}
