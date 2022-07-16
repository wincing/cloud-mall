package org.crudboy.cloud.mall.cartorder.feign;

import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 购物车模块对应的FeignClient
 */
@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {

    @PostMapping("/checkAdminRole")
    Boolean checkAdminRole(@RequestBody User user);

    @PostMapping("/getuser")
    User getUserById(@RequestParam("userId") Integer userId);

    @GetMapping("/token/get")
    Integer get(@RequestParam("token") String token);
}
