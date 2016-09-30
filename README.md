WORK IN PROGRESS, PLEASE COME BACK LATER

# Better SQL in Java

WARNING : this project is only a POC, don't use it for real use cases.

## The question

I'm not happy with current solutions for SQL ORM in Java : they all suck.

Is it a limitation of Java, or can we do better ?

## What I want

What I want is rely on Java paradigm :

- As much type safety as possible
- As much compile-time validation as possible
- If you can't validate something during compilation, do it during startup 
- Clean API
- No type cast
- No dark magic at runtime

## The idea

Describe your SQL model in XML and use this XML to generate Java code.

The Java code generation should enable to fine tune the query API for maximum safety.

With this grand plan, see how good is the API can be.

## Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema targetPackage="net.archiloque.bsoij.example">
    <model id="customer" tableName="customer">
        <primaryKey column="customer_id"/>
        <column name="customer_id" type="Integer" nullable="false"/>
        <column name="name" type="String" nullable="false"/>
        <column name="email" type="String" nullable="true"/>
        <column name="birth" type="Date" nullable="true"/>
    </model>
    <model id="order" tableName="order">
        <primaryKey column="order_id"/>
        <column name="order_id" type="Integer" nullable="false"/>
        <column name="date" type="Date" nullable="false"/>
        <column name="delivery_date" type="Date" nullable="true"/>
        <column name="amount" type="Integer" nullable="false"/>
        <column name="customer_id" type="Integer" nullable="false"/>
        <foreignKey name="customer" reverseName="orders" column="customer_id" references="customer"/>
    </model>
</schema>
```

```java
CustomerSelect customerSelect = CustomerModel.
        select().
        where(CustomerModel.NAME, Criteria.stringEquals("Roger"));
CustomerOrderSelect customerOrderSelect = customerSelect.joinOrders();
CustomerOrderSelect customerOrderSelectWithDeliveryDate = customerOrderSelect.where(OrderModel.DELIVERY_DATE, Criteria.dateIsNotNull());
Stream<CustomerOrderModel> customerOrderModelStream = customerOrderSelectWithDeliveryDate.fetch();
customerOrderModelStream.forEach(customerOrderModel -> {
    CustomerModel customer = customerOrderModel.getCustomer();
    OrderModel order = customerOrderModel.getOrder();
    System.out.println(customer);
    System.out.println(order);
});
```