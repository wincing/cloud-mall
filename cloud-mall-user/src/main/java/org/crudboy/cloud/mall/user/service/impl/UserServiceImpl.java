package org.crudboy.cloud.mall.user.service.impl;


import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.crudboy.cloud.mall.common.util.MD5Utils;
import org.crudboy.cloud.mall.user.model.dao.UserMapper;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.crudboy.cloud.mall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    /**
     * 用户注册
     * @param username
     * @param password
     * @throws MallException
     */
    @Override
    public void register(String username, String password) throws MallException {
        // 重名判断
        User user = userMapper.selectByName(username);
        if (user != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }

        try {
            user = new User();
            user.setUsername(username);
            user.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int count = userMapper.insertSelective(user);

        if (count == 0) {
            throw new MallException(MallExceptionEnum.INSERT_FAILED);
        }
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     * @throws MallException
     */
    @Override
    public User login(String username, String password) throws MallException {
        String hashedPassword = null;

        try {
            hashedPassword = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        User user = userMapper.selectForLogin(username, hashedPassword);
        if (user == null) {
            throw new MallException(MallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    /**
     * 用户更新个人信息
     * @param user
     * @throws MallException
     */
    @Override
    public void updateInfo(User user) throws MallException {
        int count = userMapper.updateByPrimaryKeySelective(user);

        if (count > 1) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 管理员校验
     * @param user
     * @return
     */
    @Override
    public boolean isAdmin(User user) {
        return user.getRole().equals(2);
    }
}
