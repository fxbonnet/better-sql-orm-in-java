WORK IN PROGRESS, PLEASE COME BACK LATER

# Better SQL in Java

WARNING: this project is only a toy POC, don't use it for real use cases.

## The question

I'm not happy with current solutions for SQL ORM in Java because they all are bad in a way or another:
- Hibernate is a half-assed solution
- JPA took some parts of it but missed some crucial ones (aka « the Java way™ ») so it's mostly unusable as is
- They are stuck in Java 3 or in Java 6
- Bad magic everywhere including horrid runtime code generation
- They are hard to debug
- They want to make SQL look like Java classes, and the impedance strikes back
- The error management is a mess: you got NullPointerExceptions or ClassCastExceptions whenever you forgot a "mandatory" annotation 
- Lazy loading + relations = total hell

Is it a limitation of Java, or can we do better?

## What I want

What I want is rely on Java paradigm:

- As much type safety as possible
- As much compile-time validation as possible
- If you can't validate something during compilation, do it during startup 
- No dark magic at runtime

Plus : 
- Don't try to make SQL access look like Java
- Clean API
- Plain code

## The idea

Describe your SQL model in XML and use this XML to generate Java code.

The Java code generation should enable to fine tune the query API for maximum safety.

With this grand plan, see how good the API can be.

## Example

Have a look at the `bsoj-example` project for a usage example.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema targetPackage="net.archiloque.bsoij.example">
    <model id="customer" tableName="customer">
        <primaryKey column="customer_id"/>
        <column name="customer_id" type="Long" nullable="false"/>
        <column name="name" type="String" nullable="false"/>
        <column name="email" type="String" nullable="true"/>
        <column name="birth" type="Date" nullable="true"/>
    </model>
    <model id="order" tableName="order">
        <primaryKey column="order_id"/>
        <column name="order_id" type="Long" nullable="false"/>
        <column name="date" type="Date" nullable="false"/>
        <column name="delivery_date" type="Date" nullable="true"/>
        <column name="amount" type="Integer" nullable="false"/>
        <column name="customer_id" type="Long" nullable="false"/>
        <foreignKey name="customer" reverseName="orders" column="customer_id" references="customer"/>
    </model>
</schema>
```

```java
CustomerSelect customerSelect = CustomerModel.
  select().
  where(
    CustomerModel.NAME, // you can only filter by fields related to Customers 
    Criteria.stringEquals("Roger") // Criteria must match the field type
  );

// join with another table : indicated by the return type
CustomerOrderSelect customerOrderSelect = customerSelect.joinOrders();

CustomerOrderSelect customerOrderSelectWithDeliveryDate = customerOrderSelect.
  where(
    OrderModel.DELIVERY_DATE, // joined can be filter by fields of both models 
    Criteria.dateIsNotNull() // the criteria is available because the column is nullable
  );

// order by available fields
CustomerOrderSelect orderedCustomerOrderSelectWithDeliveryDate = customerOrderSelectWithDeliveryDate.
  order(OrderModel.DATE, Order.ASC);

// fetch the data, the Stream is AutoCloseable
try (Stream<CustomerOrderModel> customerOrderModelStream = orderedCustomerOrderSelectWithDeliveryDate.fetch()) {
  customerOrderModelStream.forEach(customerOrderModel -> {
    // the join result holds links to individual models
    CustomerModel customer = customerOrderModel.getCustomer();
    OrderModel order = customerOrderModel.getOrder();
    System.out.println(customer);
    System.out.println(order);

    // "fetchXX" shows that a query is executed = explicit lazy loading
    CustomerModel customerModel = order.fetchCustomer();
  });
 }
```

# Requirements

- Java 8
- Maven to build
- H2

## Test it

- Clone the project
- Build the root project `mvn clean install`
- Build the example project in the `bsoij-example` directory : `mvn clean install` and play with it

## Limitations

Mostly everything that is not in the example 😅

- Only a POC, don't use for real use
- Only simple filtering
- Only work with H2
- Only simple joins
- Only long primary keys
- Only simple types
- Date handling is probably broken when using timestamp
- Minimal test