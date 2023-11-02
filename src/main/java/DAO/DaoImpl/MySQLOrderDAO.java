

package DAO.DaoImpl;



import DAO.MySQLConnectionManager;
import DAO.MySQLDAOConfig;
import DAO.OrderDAO;
import entity.Order;
import entity.ProductOrder;
import entity.UserOrder;
import entity.enums.Role;
import entity.enums.OrderStatus;

import java.sql.*;
import java.time.LocalDateTime;


public class MySQLOrderDAO implements OrderDAO {

    private final MySQLConnectionManager connectionManager;

    public MySQLOrderDAO(MySQLDAOConfig config) {
        connectionManager = new MySQLConnectionManager(config);
    }

    private static final String ADD_ORDER = "INSERT INTO `order` (date, status, delivery) VALUES (?, DEFAULT, ?)";
    private static final String UPDATE_STATUS = "UPDATE `order` SET status=? WHERE id=?";
    private static final String INSERT_PRODUCTS_ORDER = "INSERT INTO `product_order` (product_id, order_id, amount, price) VALUES (?, ?, ?, ?)";
    private static final String INSERT_ORDER_USER = "INSERT INTO `user_order` (order_id, user_id, datetime) VALUES (?, ?, DEFAULT)";
    private static final String GET_ORDER_BY_ID = "SELECT * from `order` WHERE id=?";
    private static final String DELETE_ORDER = "DELETE FROM `order` WHERE id = ?";
    private static final String GET_PRODUCT_ORDER = "SELECT * FROM `product_order` WHERE order_id=?";
    private static final String GET_USER_ORDER = "SELECT * FROM `user_order` WHERE order_id=?";
    private static final String GET_ROLE = "SELECT role FROM `user` WHERE id=?";
    private static final String DELETE_USER_ORDER_BY_ORDER_ID = "DELETE FROM user_order WHERE order_id = ?";


    @Override
    public long createOrder(Order order) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = connectionManager.getConnection(false);
            st = conn.prepareStatement(ADD_ORDER, Statement.RETURN_GENERATED_KEYS);
            st.setObject(1, LocalDateTime.now());
            st.setLong(2, order.getDelivery().getId());
            st.executeUpdate();
            ResultSet generatedKeys = st.getGeneratedKeys();

            st = conn.prepareStatement(INSERT_PRODUCTS_ORDER);
            if (generatedKeys.next()) {
                order.setId(generatedKeys.getLong(1));
                for (ProductOrder productOrder : order.getProductsInOrder()) {
                    setProductOrder(order.getId(), productOrder, st);
                    st.addBatch();
                }
                st.executeBatch();

                st = conn.prepareStatement(INSERT_ORDER_USER);
                setClient(order, st);
                st.executeUpdate();
            }
            conn.commit();
            return order.getId();
        } catch (Exception e) {
            connectionManager.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            connectionManager.close(st, conn);
        }
    }


    @Override
    public Order getOrderById(long orderId) {
        Order order = new Order();
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_ORDER_BY_ID)) {
                int k = 0;
                ps.setLong(++k, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        order = mapOrder(rs);
                        try (PreparedStatement prs = con.prepareStatement(GET_USER_ORDER)) {
                            int l = 0;
                            prs.setLong(++l, order.getId());
                            UserOrder userOrder;
                            try (ResultSet resultSet = prs.executeQuery()) {
                                while (resultSet.next()) {
                                    userOrder = mapUserOrder(resultSet);
                                    Role role1 = null;
                                    try (PreparedStatement preparedStatement = con.prepareStatement(GET_ROLE)) {
                                        int m = 0;
                                        preparedStatement.setLong(++m, userOrder.getUserId());
                                        try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
                                            while (resultSet1.next()) {
                                                role1 = getRole(userOrder.getUserId());
                                            }
                                        }
                                    }
                                    order.putUser(role1, userOrder);
                                }
                            }
                        }
                        getProductOrder(order, con);
                    }
                    return order;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void getProductOrder(Order order, Connection con) throws SQLException {
        try (PreparedStatement prs = con.prepareStatement(GET_PRODUCT_ORDER)) {
            int l = 0;
            prs.setLong(++l, order.getId());
            try (ResultSet resultSet = prs.executeQuery()) {
                while (resultSet.next()) {
                    try (PreparedStatement prst = con.prepareStatement("SELECT name FROM product WHERE id=?")) {
                        int b = 0;
                        prst.setLong(++b, resultSet.getLong("shoe_id"));
                        try (ResultSet rs = prst.executeQuery()) {
                            while (rs.next()) {
                                order.addProduct(mapProductOrder(resultSet, rs));
                            }
                        }
                    }
                }
            }
        }


    }


    private Role getRole(Long id) {
        Role role = null;
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_ROLE)) {
                int k = 0;
                ps.setLong(++k, id);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        role = Role.valueOf(resultSet.getString("role").toUpperCase());
                    }
                    return role;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus status)  {
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE_STATUS)) {
                int k = 0;
                ps.setString(++k, String.valueOf(status));
                ps.setLong(++k, orderId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setProductOrder(long orderId, ProductOrder productOrder, PreparedStatement st) throws
            SQLException {
        int k = 0;
        st.setLong(++k, orderId);
        st.setLong(++k, productOrder.getProductId());
        st.setBigDecimal(++k, productOrder.getPrice());
        st.setInt(++k, productOrder.getAmount());
    }

    private static void setClient(Order order, PreparedStatement st) throws SQLException {
        st.setLong(1, order.getId());
        st.setLong(2, order.getUsersInOrder().get(Role.CLIENT).getUserId());
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
        o.setDatetime(date);
        o.setStatus(OrderStatus.valueOf(rs.getString("status").toUpperCase()));
        return o;
    }

    private ProductOrder mapProductOrder(ResultSet resultSet, ResultSet rs) throws SQLException {
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductId(resultSet.getLong("product_id"));
        productOrder.setPrice(resultSet.getBigDecimal("price"));
        productOrder.setAmount(resultSet.getInt("amount"));
        return productOrder;
    }

    private UserOrder mapUserOrder(ResultSet rs) throws SQLException {
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(rs.getLong("user_id"));
        userOrder.setDateTime(rs.getTimestamp("datetime").toLocalDateTime());
        return userOrder;
    }

    @Override
    public void deleteOrder(long id) {
        try (Connection connection = connectionManager.getConnection()) {
            // Видаляємо пов'язані записи в таблиці user_order
            try (PreparedStatement userOrderStatement = connection.prepareStatement(DELETE_USER_ORDER_BY_ORDER_ID)) {
                userOrderStatement.setLong(1, id);
                userOrderStatement.executeUpdate();
            }


            try (PreparedStatement orderStatement = connection.prepareStatement(DELETE_ORDER)) {
                orderStatement.setLong(1, id);
                orderStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
