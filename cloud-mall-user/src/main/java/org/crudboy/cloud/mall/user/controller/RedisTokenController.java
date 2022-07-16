package org.crudboy.cloud.mall.user.controller;

import org.crudboy.cloud.mall.user.service.RedisTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisTokenController {

    @Autowired
    RedisTokenService redisTokenService;

    @PostMapping("/token/add")
    public void add(@RequestParam("token") String token,
                    @RequestParam("userId") int userId) {
        redisTokenService.add(token, userId);
    }

    @GetMapping("/token/get")
    public Integer get(@RequestParam("token") String token) {
        return redisTokenService.get(token);
    }

    @PostMapping("/token/delete")
    public void delete(@RequestParam("token") String token) {
        redisTokenService.delete(token);
    }
}
