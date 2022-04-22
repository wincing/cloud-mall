package org.crud.cloud.mall.zuul.feign;

import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("cloud-mall-user")
public interface UserFeignClient {

    @PostMapping("/checkAdminRole")
    Boolean checkAdminRole(@RequestBody User user);
}
