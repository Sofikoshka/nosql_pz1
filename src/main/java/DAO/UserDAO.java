package DAO;

import entity.User;


public interface UserDAO {

    long createUser(User user);

    User findById(long id);


    void updateUser(long userId, String newEmail);

    void deleteUser(long id);
}