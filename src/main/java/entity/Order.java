package entity;


import entity.enums.OrderStatus;
import entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Order {
    private long id;
    private LocalDateTime datetime;
    private OrderStatus status;
    private List<ProductOrder> productsInOrder;
    private Map<Role, UserOrder> usersInOrder;
    private Delivery delivery;

    public Order() {
        productsInOrder = new ArrayList<>();
        usersInOrder = new EnumMap<>(Role.class);
    }

    public Order(long clientId, Delivery delivery, List<ProductOrder> productsInOrder) {
        this.delivery = delivery;
        this.productsInOrder = productsInOrder;

    }

    public void addProduct(ProductOrder productOrder) {
        productsInOrder.add(productOrder);
    }
    public void putUser(Role role, UserOrder userOrder) {
        usersInOrder.put(role, userOrder);
    }



}
