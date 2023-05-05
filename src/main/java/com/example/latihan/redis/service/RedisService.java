package com.example.latihan.redis.service;

import com.example.latihan.redis.entity.Request;
import com.example.latihan.redis.entity.table.Book;
import com.example.latihan.redis.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bni.wsslib.dto.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    @Autowired
    public final BookRepository bookRepository;

    @Autowired
    public final RedisTemplate redisTemplate;

    @Autowired
    public final CacheManager cacheManager;

    public ResponseEntity<ResponseService> getBooks() {
        StopWatch sw = new StopWatch();
        sw.start();
        List<Book> books = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            // pengecekan key books apakah ada atau tidak
            if (redisTemplate.opsForValue().get("books") != null) {
                // jika ada ambil data dari cache (redis)
                log.info("ambil data dari cache");
                books = (List<Book>) redisTemplate.opsForValue().get("books");
            } else {
                log.info("ambil data dari db");
                // jika tidak, ambil data dari database
                books = getAllDataBook();

                // menyimpan data menggunakan key books
                redisTemplate.opsForValue().set("books", books);
//                redisTemplate.expire("books", 1, TimeUnit.MINUTES);
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

    public ResponseEntity<ResponseService> getBooksDb() {
        StopWatch sw = new StopWatch();
        sw.start();

        try {
            log.info("ambil semua data dari db");

            List<Book> book = bookRepository.findAll();

            ResponseService responseService = ResponseService.builder()
                    .data(book)
                    .build();
            return ResponseEntity.ok(responseService);
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

    public void updateCacheRedis() {
        List<Book> books = bookRepository.findAll();
        redisTemplate.opsForValue().set("books", books);
    }

    public ResponseEntity<ResponseService> addBook(Request request) {
        Book book = Book.builder()
                .id(request.getId())
                .bookName(request.getBookName())
                .author(request.getAuthor())
                .genre(request.getGenre())
                .build();

        try {
            saveBook(book);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResponseService responseService = ResponseService.builder().data(book).build();

        return ResponseEntity.ok(responseService);
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
        updateCacheRedis();
    }

    public ResponseEntity<ResponseService> updateBook(Request request) {
        Book book = Book.builder()
                .id(request.getId())
                .bookName(request.getBookName())
                .author(request.getAuthor())
                .genre(request.getGenre())
                .build();

        try {
            saveBook(book);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResponseService responseService = ResponseService.builder().data(book).build();

        return ResponseEntity.ok(responseService);
    }

    public ResponseEntity<ResponseService> deleteBook(String id) {
        bookRepository.deleteById(Integer.parseInt(id));

        updateCacheRedis();

        ResponseService responseService = ResponseService.builder().status("success").message("deleted").build();

        return ResponseEntity.ok(responseService);
    }

    public ResponseEntity<ResponseService> findBook(String id) {
        Book book;
        ObjectMapper mapper = new ObjectMapper();
        if (redisTemplate.opsForValue().get("book_" + id) != null) {
            log.info("cari data di cache");
            book = mapper.convertValue(redisTemplate.opsForValue().get("book_" + id), Book.class);
        } else {
            log.info("cari data di db");
            book = getBookById(id);
            redisTemplate.opsForValue().set("book_"+id, book);
        }
        updateCacheRedis();

        ResponseService responseService = ResponseService.builder().status("success").data(book).build();

        return ResponseEntity.ok(responseService);
    }

    @Cacheable(value = "books", key="#books.id")
    public Book getBookById(String id){
        return bookRepository.findById(Integer.parseInt(id)).orElse(null);
    }


}
