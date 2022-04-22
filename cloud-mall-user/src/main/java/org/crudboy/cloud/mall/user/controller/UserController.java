package org.crudboy.cloud.mall.user.controller;


import com.netflix.ribbon.proxy.annotation.Http;
import org.crudboy.cloud.mall.common.common.ApiRestResponse;
import org.crudboy.cloud.mall.common.common.Constant;
import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.crudboy.cloud.mall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import static org.crudboy.cloud.mall.common.common.Constant.MALL_USER;

/**
 * 用户控制器
 */

@RestController
public class UserController {


    @Autowired
    UserService userService;

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

    @PostMapping("/login")
    public ApiRestResponse login(@RequestParam String username, @RequestParam String password,
                                 HttpSession session) throws MallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        User user = userService.login(username, password);
        // session中不能保存用户密码
        user.setPassword(null);
        session.setAttribute(MALL_USER, user);

        return ApiRestResponse.success(user);
    }

    @PostMapping("/user/update")
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws MallException {
        User currentUser = (User)session.getAttribute(MALL_USER);
        if (currentUser == null) {
            throw new MallException(MallExceptionEnum.NEED_LOGIN);
        }

        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInfo(user);

        return ApiRestResponse.success();
    }

    @PostMapping("/user/logout")
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(MALL_USER);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/login")
    public ApiRestResponse adminLogin(@RequestParam String username, @RequestParam String password,
                                 HttpSession session) throws MallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        User user = userService.login(username, password);
        if (userService.isAdmin(user)) {
            user.setPassword(null);
            session.setAttribute(MALL_USER, user);
        } else {
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
        return ApiRestResponse.success(user);
    }

    @PostMapping("/checkAdminRole")
    public Boolean checkAdminRole(@RequestBody User user) {
        return userService.isAdmin(user);
    }

    /**
     * 获取当前登录的User对象
     * @param session
     * @return
     */
    @GetMapping("/getCurrentUser")
    public User getCurrentUser(HttpSession session) {
        return (User)session.getAttribute(MALL_USER);
    }
}
