package net.archiloque.bsoij.example;

import net.archiloque.bsoij.base_classes.Criteria;
import net.archiloque.bsoij.base_classes.Order;
import net.archiloque.bsoij.example.model.CustomerModel;
import net.archiloque.bsoij.example.model.CustomerOrderModel;
import net.archiloque.bsoij.example.model.OrderModel;
import net.archiloque.bsoij.example.select.CustomerOrderSelect;
import net.archiloque.bsoij.example.select.CustomerSelect;

import java.util.stream.Stream;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

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

        // fetch the data
        Stream<CustomerOrderModel> customerOrderModelStream = orderedCustomerOrderSelectWithDeliveryDate.fetch();
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
}
