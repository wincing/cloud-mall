package org.crud.cloud.mall.zuul.feign;

import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cloud-mall-user") // 对应于应用名 spring.application.name
public interface UserFeignClient {

    @PostMapping("/checkAdminRole")
    Boolean checkAdminRole(@RequestBody User user);

    @PostMapping("/getuser")
    User getUserById(@RequestParam("userId") Integer userId);

    @PostMapping("/token/add")
    void add(@RequestParam("token") String token, @RequestParam("userId") int userId);

    @GetMapping("/token/get")
    Integer get(@RequestParam("token") String token);

    @PostMapping("/token/delete")
    void delete(@RequestParam("token") String token);
}
