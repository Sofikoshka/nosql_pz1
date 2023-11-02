package DAO;


import entity.Order;
import entity.enums.OrderStatus;


public interface OrderDAO {

    long createOrder(Order order);


    Order getOrderById(long id);



    void updateStatus(Long orderId, OrderStatus status) ;


    void deleteOrder(long id);
}
