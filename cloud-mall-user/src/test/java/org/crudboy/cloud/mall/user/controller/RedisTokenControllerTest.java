package org.crudboy.cloud.mall.user.controller;

import org.crudboy.cloud.mall.user.model.pojo.User;
import org.crudboy.cloud.mall.user.service.RedisTokenService;
import org.crudboy.cloud.mall.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTokenControllerTest {

    @Autowired
    RedisTokenService redisTokenService;

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void addTokenTest() {
        String key = "adfasdfadfadfa";
        redisTokenService.add(key, 18);
        Integer userId = redisTokenService.get(key);
        System.out.println(userId);
    }

    @Test
    public void getUserTest() {
        String key = "adfasdfadfadfa";
        User user = (User)redisTemplate.opsForValue().get(key);
        System.out.println(user);
    }
}
