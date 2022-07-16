package org.crudboy.cloud.mall.user.service;

import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Controller
public class RedisTokenService {

    public static final int EXPIRED_TIME =  2 * 60 * 60;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    // todo 增加map进行双向映射

    /** <key, value> - <token, userID> **/
    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();

    public void add(String token, int userId) {
        User user = userService.getUserById(userId);
        map.put(token, userId);
        redisTemplate.opsForValue().set(token, user, EXPIRED_TIME, TimeUnit.SECONDS);
    }

    public Integer get(String token) {
        return map.get(token);
    }

    public void delete(String token) {
        redisTemplate.delete(token);
        map.remove(token);
    }

}
