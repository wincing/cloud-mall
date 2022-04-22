package org.crudboy.cloud.mall.cartorder.feign;

import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

import static org.crudboy.cloud.mall.common.common.Constant.MALL_USER;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {

    @GetMapping("/getCurrentUser")
    User getCurrentUser();

    @PostMapping("/checkAdminRole")
    Boolean checkAdminRole(@RequestBody User user);
}
