package com.example.latihan.redis.controller;

import com.example.latihan.redis.entity.BookResponse;
import com.example.latihan.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisController {

    @Autowired
    public final RedisService redisService;

    @GetMapping("/books")
    ResponseEntity<BookResponse> getAllBook(){

        BookResponse bookResponse = BookResponse.builder()
                .build();

        return ResponseEntity.ok(bookResponse);
    }

}
