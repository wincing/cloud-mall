package org.crudboy.cloud.mall.user.service;

import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.user.model.pojo.User;

public interface UserService {

    void register(String username, String password) throws MallException;

    User login(String username, String password) throws MallException;

    void updateInfo(User user) throws MallException;

    boolean isAdmin(User user);
}
