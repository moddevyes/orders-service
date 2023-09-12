# orders-service
Commerce Domain - Orders Service



### Refinements

FIND:
- byId
- all

CREATE:
- order (created by someone with an Account and Address)
- generate the order number after initial fields are validated
- assign current date for order date
- assign shipping address to address from account
- allow 0 or more order line items (Think Empty Shopping Cart)
- compute line item total price and assign total price (to populate) each item with quantity, price and total price for order

UPDATE:

change shipment address

order line item
- shipment id
- product id
- quantity, price
- compute total price

DELETE: order + order line items


### Inter-Service Communication

1. How are the services sharing information?
2. Account with an Address *creates* a new Order.
3. Update shipment id, is there a shipment?
4. Update product id, does the product exist, if so, get the quantity and price for that order line item?
5. Nothing about order confirmed, so send order line items and other data (event, service endpoint) to create a new shipment?
6. DELETE order is cancellation?