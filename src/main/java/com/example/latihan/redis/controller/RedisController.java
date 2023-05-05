package com.example.latihan.redis.controller;

import com.example.latihan.redis.entity.Request;
import com.example.latihan.redis.service.RedisService;
import id.co.bni.wsslib.dto.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RedisController {

    @Autowired
    public final RedisService redisService;
    
    @Autowired
    public final CacheManager cachcheManager;

    @GetMapping("/books")
    public ResponseEntity<ResponseService> getAllBook(){
        return redisService.getBooks();
    }

    @PostMapping("/add-book")
    public ResponseEntity<ResponseService> addBook(
            @RequestBody Request request
            ){
        return redisService.addBook(request);
    }

    @PutMapping("/update-book")
    public ResponseEntity<ResponseService> updateBook(
            @RequestBody Request request
    ){
        return redisService.updateBook(request);
    }
    @DeleteMapping("/delete-book")
    public ResponseEntity<ResponseService> deleteBook(
            @RequestParam String id
    ){
        return redisService.deleteBook(id);
    }
    @GetMapping("/find-book")
    public ResponseEntity<ResponseService> findBook(
            @RequestParam String id
    ){
        return redisService.findBook(id);
    }



    @Tag(name = "DB")
    @GetMapping("/books/from-db")
    public ResponseEntity<ResponseService> getBooksDb(){
        return redisService.getBooksDb();
    }

    @Tag(name = "redis-configuration")
    @GetMapping("/update-redis")
    public void updateCache(){
        redisService.updateCacheRedis();
    }

}
