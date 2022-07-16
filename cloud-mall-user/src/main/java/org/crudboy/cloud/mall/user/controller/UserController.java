package org.crudboy.cloud.mall.user.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.crudboy.cloud.mall.common.common.ApiRestResponse;
import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.crudboy.cloud.mall.user.service.RedisTokenService;
import org.crudboy.cloud.mall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static org.crudboy.cloud.mall.common.common.Constant.MALL_TOKEN;

/**
 * 用户模块路由
 */

@Slf4j
@RestController
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    RedisTokenService redisTokenService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ApiRestResponse register(@RequestParam String username,
                                    @RequestParam String password) throws MallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ApiRestResponse.error(MallExceptionEnum.PASSWORD_TO_SHORT);
        }

        userService.register(username, password);
        return ApiRestResponse.success();
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ApiRestResponse login(@RequestParam String username, @RequestParam String password,
                                 HttpServletResponse response) throws MallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        User user = userService.login(username, password);
        user.setPassword(null); // session中不能保存用户密码

        String token = UUID.randomUUID().toString();
        redisTokenService.add(token, user.getId());
        response.setHeader(MALL_TOKEN, token);
        log.info("user login completed: {} ---- token is {}", user, token);

        return ApiRestResponse.success(user);
    }

    @ApiOperation("用户个性签名更新")
    @PostMapping("/user/update")
    public ApiRestResponse updateUserInfo(HttpServletRequest request,
                                          @RequestParam String signature) throws MallException {
        String token = request.getHeader(MALL_TOKEN);
        Integer userId = redisTokenService.get(token);
        User user = userService.getUserById(userId);
        user.setPersonalizedSignature(signature);
        userService.updateInfo(user);

        return ApiRestResponse.success();
    }

    @ApiOperation("用户退出")
    @PostMapping("/user/logout")
    public ApiRestResponse logout(HttpServletRequest request) {
        String token = request.getHeader(MALL_TOKEN);
        redisTokenService.delete(token);
        return ApiRestResponse.success();
    }

    @ApiOperation("管理员登录")
    @PostMapping("/admin/login")
    public ApiRestResponse adminLogin(@RequestParam String username, @RequestParam String password,
                                 HttpServletResponse response) throws MallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        User user = userService.login(username, password);
        user.setPassword(null);

        if (userService.isAdmin(user)) {
            String token = UUID.randomUUID().toString();
            redisTokenService.add(token, user.getId());
            response.setHeader(MALL_TOKEN, token);
            log.info("user login: {} ---- token is {}", user, token);
        } else {
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
        return ApiRestResponse.success(user);
    }

    @ApiOperation("用户身份校验(用于外部服务调用)")
    @PostMapping("/checkAdminRole")
    public Boolean checkAdminRole(@RequestBody User user) {
        return userService.isAdmin(user);
    }

    @PostMapping("/getuser")
    public User getUserById(@RequestParam("userId") Integer userId) {
        return userService.getUserById(userId);
    }
}
