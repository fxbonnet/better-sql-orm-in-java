package net.archiloque.bsoij.example;

import net.archiloque.bsoij.base_classes.criteria.Criteria;
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
    }
}
