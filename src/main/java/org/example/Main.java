package org.example;

import DAO.DAOFactory;
import DAO.MySQLDAOConfig;
import DAO.ProductDAO;
import DAO.UserDAO;
import DAO.DeliveryDAO;
import DAO.OrderDAO;

import entity.*;
import entity.enums.OrderStatus;
import entity.enums.Role;
import entity.enums.Type;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        MySQLDAOConfig mySQLDAOConfig = new MySQLDAOConfig();
        mySQLDAOConfig.setType("MySQL");
        mySQLDAOConfig.setUrl("jdbc:mysql://localhost:3306/jewelry_shop?sslMode=DISABLED&serverTimezone=UTC");
        mySQLDAOConfig.setUser("root");
        mySQLDAOConfig.setPassword("root");

        DAOFactory daoFactory = new DAOFactory();

        UserDAO userDAO = daoFactory.getUserDAOInstance(mySQLDAOConfig);
        ProductDAO productDAO = daoFactory.getProductDAOInstance(mySQLDAOConfig);
        DeliveryDAO deliveryDAO = daoFactory.getDeliveryDAOInstance(mySQLDAOConfig);
        OrderDAO orderDAO = daoFactory.getOrderDAOInstance(mySQLDAOConfig);


        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        user1.setLastname("1111d");
        user1.setPhone("43565f");
        user1.setPassword("dfkoreop54");
        users.add(user1);

        List<Product> products = new ArrayList<>();
        Product p1 = new Product();
        p1.setName("gggg");
        p1.setSize(BigDecimal.valueOf(7.5));
        p1.setColor("fff");
        p1.setType(Type.RING);
        p1.setAmount(20);
        p1.setActual_price(BigDecimal.valueOf(6.89));
        products.add(p1);

        List<Delivery> deliveries = new ArrayList<>();
        Delivery d1 = new Delivery();
        d1.setCity("gggwft");
        d1.setStreet("fperg");
        d1.setHouse("s3");
        d1.setEntrance(3);
        d1.setApartment(4);
        deliveries.add(d1);




        List<Order> orders = new ArrayList<>();
        Order o1 = new Order();
        o1.setDatetime(LocalDateTime.parse("2023-01-01T00:00:00"));
        o1.setStatus(OrderStatus.ACCEPTED);
        o1.setDelivery(d1);
        orders.add(o1);


        Role role = Role.CLIENT;
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(1);
        userOrder.setDateTime(LocalDateTime.parse("2023-01-01T00:00:00"));
        o1.putUser(role, userOrder);


        System.out.println("Додавання та пошук користувачів:");

        // Вставка та пошук користувачів
        for (User user : users) {
            long userId = userDAO.createUser(user);
            User foundUser = userDAO.findById(userId);
            System.out.println("Знайдений користувач: " + foundUser);
        }

        // Оновлення Email користувача
        long userIdToUpdate = user1.getId();
        String newEmail = "ffff.email@example.com";
        System.out.println("\nОновлення Email користувача:");
        User userBeforeUpdate = userDAO.findById(userIdToUpdate);
        System.out.println("Інформація про користувача до оновлення Email: " + userBeforeUpdate);
        userDAO.updateUser(userIdToUpdate, newEmail);
        User userAfterUpdate = userDAO.findById(userIdToUpdate);
        System.out.println("Інформація про користувача після оновлення Email: " + userAfterUpdate);


        System.out.println("\nДодавання та пошук продуктів:");

        // Вставка та пошук продуктів

        for (Product product : products) {
            long productId = productDAO.createProduct(product);
            Product foundProduct = productDAO.getProductById(productId);
            System.out.println("Знайдений продукт: " + foundProduct);
        }

        System.out.println("\nОновлення кількості продуктів:");

// Оновлення кількості продуктів
        for (Product product : products) {
            long productIdToUpdate = p1.getId(); // Get the ID from the object
            System.out.println("\nОновлення кількості продукта (ID: " + productIdToUpdate + "):");
            Product productBeforeUpdate = productDAO.getProductById(productIdToUpdate);
            System.out.println("Інформація про продукт (ID: " + productIdToUpdate + ") до оновлення кількості: " + productBeforeUpdate);
            productDAO.updateProductAmount(productIdToUpdate, 40);
            Product productAfterUpdate = productDAO.getProductById(productIdToUpdate);
            System.out.println("Інформація про продукт (ID: " + productIdToUpdate + ") після оновлення кількості: " + productAfterUpdate);
        }

        System.out.println("\nОновлення ціни продуктів:");

// Оновлення ціни продуктів
        for (Product product : products) {
            long productIdToUpdatePrice = p1.getId(); // Get the ID from the object
            System.out.println("\nОновлення ціни продукта (ID: " + productIdToUpdatePrice + "):");
            Product productBeforeUpdatePrice = productDAO.getProductById(productIdToUpdatePrice);
            System.out.println("Інформація про продукт (ID: " + productIdToUpdatePrice + ") до оновлення ціни: " + productBeforeUpdatePrice);
            productDAO.updateProductPrice(productIdToUpdatePrice, BigDecimal.valueOf(88.99));
            Product productAfterUpdatePrice = productDAO.getProductById(productIdToUpdatePrice);
            System.out.println("Інформація про продукт (ID: " + productIdToUpdatePrice + ") після оновлення ціни: " + productAfterUpdatePrice);
        }


        System.out.println("\nДодавання та пошук доставок:");

        // Вставка та пошук доставок

        for (Delivery delivery : deliveries) {
            long deliveryId = deliveryDAO.createDelivery(delivery);
            Delivery foundDelivery = deliveryDAO.findById(deliveryId);
            System.out.println("Знайдена доставка: " + foundDelivery);
        }

        System.out.println("\nОновлення вулиці в доставці:");

        // Оновлення вулиці в доставці
        long deliveryIdToUpdate = d1.getId();
        String newStreet = "Shevchenko";
        System.out.println("\nОновлення вулиці в доставці:");
        Delivery deliveryBeforeUpdate = deliveryDAO.findById(deliveryIdToUpdate);
        System.out.println("Інформація про доставку до оновлення вулиці: " + deliveryBeforeUpdate);
        deliveryDAO.updateStreet(deliveryIdToUpdate, newStreet);
        Delivery deliveryAfterUpdate = deliveryDAO.findById(deliveryIdToUpdate);
        System.out.println("Інформація про доставку після оновлення вулиці: " + deliveryAfterUpdate);


        System.out.println("\nДодавання та пошук замовлень:");

// Вставка та пошук замовлень

        for (Order order : orders) {
            long orderId = orderDAO.createOrder(order);
            Order foundOrder = orderDAO.getOrderById(orderId);
            System.out.println("Знайдене замовлення: " + foundOrder);
        }

// Оновлення статусу замовлення
        long orderIdToUpdate = o1.getId();
        OrderStatus newStatus = OrderStatus.PACKED;
        System.out.println("\nОновлення статусу замовлення:");
        Order orderBeforeUpdate = orderDAO.getOrderById(orderIdToUpdate);
        System.out.println("Інформація про замовлення до оновлення статусу: " + orderBeforeUpdate);
        orderDAO.updateStatus(orderIdToUpdate, newStatus);
        Order orderAfterUpdate = orderDAO.getOrderById(orderIdToUpdate);
        System.out.println("Інформація про замовлення після оновлення статусу: " + orderAfterUpdate);

        for (Order order : orders) {
            orderDAO.deleteOrder(order.getId());
            System.out.println("Видалено замовлення з ID: " + order.getId());
        }
        // Видалення користувачів
        for (User user : users) {
            userDAO.deleteUser(user.getId());
            System.out.println("Видалено користувача з ID: " + user.getId());
        }

        // Видалення продуктів
        for (Product product : products) {
            productDAO.deleteProduct(product.getId());
            System.out.println("Видалено продукт з ID: " + product.getId());
        }
        // Видалення замовлень


        // Видалення доставок
        for (Delivery delivery : deliveries) {
            deliveryDAO.deleteDelivery(delivery.getId());
            System.out.println("Видалено доставку з ID: " + delivery.getId());
        }



    }

}

