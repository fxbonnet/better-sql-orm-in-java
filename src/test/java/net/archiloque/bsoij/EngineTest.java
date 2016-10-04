package net.archiloque.bsoij;

import net.archiloque.bsoij.base_classes.Criteria;
import net.archiloque.bsoij.base_classes.Sort;
import net.archiloque.bsoij.test.model.CustomerModel;
import net.archiloque.bsoij.test.model.CustomerOrderModel;
import net.archiloque.bsoij.test.model.OrderModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test the engine.
 */
public class EngineTest extends AbstractBSOIJTest {

    @Before
    @Override
    public void initialize() throws Exception {
        super.initialize();
        EngineSingleton.setEngine(engine);
    }

    @Test
    public void testInsertion() throws Exception {
        createCustomersTable();
        CustomerModel customerModel = new CustomerModel();
        customerModel.setName("Roger");
        customerModel.setEmail("roger@example.com");
        customerModel.setBirth(Date.from(Instant.EPOCH));
        engine.insert(customerModel);
        assertNotNull(customerModel.getCustomerId());
    }

    @Test
    public void testSimpleSelect() throws Exception {
        createCustomersTable();
        CustomerModel customerModel = insertCustomerModel("Roger", "roger@example.com", Date.from(Instant.EPOCH));
        List<CustomerModel> customerModels = new ArrayList<>();
        try(Stream<CustomerModel> stream = CustomerModel.select().fetch()) {
            stream.forEach(cm -> {
                assertEquals(customerModel.getCustomerId(), cm.getCustomerId());
                assertEquals(customerModel.getName(), cm.getName());
                assertEquals(customerModel.getEmail(), cm.getEmail());
                customerModels.add(cm);
            });
        }
        assertEquals(1, customerModels.size());
    }

    @Test
    public void testSimpleSelectCriteria() throws Exception {
        createCustomersTable();
        insertCustomerModel("Roger", "roger@example.com", Date.from(Instant.EPOCH));
        CustomerModel customerLuke = insertCustomerModel("Luke", "luke@example.com", Date.from(Instant.EPOCH));
        List<CustomerModel> customerModels = new ArrayList<>();
        try(Stream<CustomerModel> stream = CustomerModel.
                select().
                where(CustomerModel.NAME, Criteria.stringEquals("Luke"))
                .fetch()) {
            stream.forEach(cm -> {
                assertEquals(customerLuke.getCustomerId(), cm.getCustomerId());
                assertEquals(customerLuke.getName(), cm.getName());
                assertEquals(customerLuke.getEmail(), cm.getEmail());
                customerModels.add(cm);
            });
        }
        assertEquals(1, customerModels.size());
    }


    @Test
    public void testSimpleSelectOrder() throws Exception {
        createCustomersTable();
        CustomerModel customerRoger = insertCustomerModel("Roger", "roger@example.com", Date.from(Instant.EPOCH));
        CustomerModel customerLuke = insertCustomerModel("Luke", "luke@example.com", Date.from(Instant.EPOCH));

        Optional<CustomerModel> customerModelOptional = CustomerModel.select().order(CustomerModel.NAME, Sort.Order.ASC).fetchFirst();
        assertTrue(customerModelOptional.isPresent());
        CustomerModel customerModel = customerModelOptional.get();
        assertEquals(customerLuke.getCustomerId(), customerModel.getCustomerId());
        assertEquals(customerLuke.getName(), customerModel.getName());
        assertEquals(customerLuke.getEmail(), customerModel.getEmail());

        customerModelOptional = CustomerModel.select().order(CustomerModel.NAME, Sort.Order.DESC).fetchFirst();
        assertTrue(customerModelOptional.isPresent());
        customerModel = customerModelOptional.get();
        assertEquals(customerRoger.getCustomerId(), customerModel.getCustomerId());
        assertEquals(customerRoger.getName(), customerModel.getName());
        assertEquals(customerRoger.getEmail(), customerModel.getEmail());

    }

    @Test
    public void testMultipleSelect() throws Exception {
        createCustomersTable();
        createOrdersTable();
        CustomerModel customerModel = insertCustomerModel("Roger", "roger@example.com", Date.from(Instant.EPOCH));
        OrderModel orderModel = insertOrderModel(10, Date.from(Instant.EPOCH), null, customerModel);

        List<CustomerOrderModel> customerOrderModels = new ArrayList<>();
        try(Stream<CustomerOrderModel> stream = CustomerModel.select().joinOrders().fetch()) {
            stream.forEach(com -> {
                assertEquals(customerModel.getCustomerId(), com.getCustomer().getCustomerId());
                assertEquals(customerModel.getName(), com.getCustomer().getName());
                assertEquals(customerModel.getEmail(), com.getCustomer().getEmail());
                assertEquals(orderModel.getCustomerId(), com.getOrder().getCustomerId());
                assertEquals(orderModel.getAmount(), com.getOrder().getAmount());
                customerOrderModels.add(com);
            });
        }
        assertEquals(1, customerOrderModels.size());
    }


    @NotNull
    private  CustomerModel insertCustomerModel(
            @NotNull String name,
            @NotNull String email,
            @NotNull Date birthDate
    ) throws SQLException {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setName(name);
        customerModel.setEmail(email);
        customerModel.setBirth(birthDate);
        engine.insert(customerModel);
        return customerModel;
    }

    @NotNull
    private OrderModel insertOrderModel(
            int amount,
            @NotNull Date date,
            @Nullable Date deliveryDate,
            @NotNull CustomerModel customerModel

    ) throws SQLException {
        OrderModel orderModel = new OrderModel();
        orderModel.setAmount(amount);
        orderModel.setDate(date);
        orderModel.setDeliveryDate(deliveryDate);
        orderModel.setCustomer(customerModel);
        engine.insert(orderModel);
        return orderModel;
    }


}
