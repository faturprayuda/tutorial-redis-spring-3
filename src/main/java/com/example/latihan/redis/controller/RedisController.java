package com.example.latihan.redis.controller;

import com.example.latihan.redis.entity.Response;
import com.example.latihan.redis.service.RedisService;
import id.co.bni.wsslib.dto.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisController {

    @Autowired
    public final RedisService redisService;
    
    @Autowired
    public final CacheManager cacheManager;

    @GetMapping("/books")
    public ResponseEntity<ResponseService> getAllBook(){
        return redisService.getBooks();
    }

}
