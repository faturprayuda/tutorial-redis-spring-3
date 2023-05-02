package com.example.latihan.redis.service;

import com.example.latihan.redis.entity.Response;
import com.example.latihan.redis.entity.table.Book;
import com.example.latihan.redis.repository.BookRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bni.wsslib.dto.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    @Autowired
    public final BookRepository bookRepository;

    @Autowired
    public final RedisTemplate redisTemplate;

    public ResponseEntity<ResponseService> getBooks() {
        StopWatch sw = new StopWatch();
        sw.start();
        List<Book> books = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            // pengecekan key books apakah ada atau tidak
            if (redisTemplate.opsForValue().get("books") != null) {
                // jika ada ambil data dari cache (redis)
                books = (List<Book>) redisTemplate.opsForValue().get("books");
            } else {
                // jika tidak, ambil data dari database
                books = getAllDataBook();

                // menyimpan data menggunakan key books
                redisTemplate.opsForValue().set("books", books);
                redisTemplate.expire("books", 1, TimeUnit.MINUTES);
            }

            ResponseService responseService = ResponseService.builder()
                    .code("")
                    .status("")
                    .message("")
                    .data(books)
                    .build();
            return ResponseEntity.ok(responseService);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } finally {
            sw.stop();
            log.info("duration to get all data : {}ms", sw.getTotalTimeMillis());
        }
    }

    // fungsi yg bertuugas untuk mengambil data dari database
    // serta mengatur cache dari spring menuju redis
    @Cacheable(value = "books")
    public List<Book> getAllDataBook() {
        return bookRepository.findAll();
    }
}
