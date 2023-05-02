package com.example.latihan.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private String bookName;
    private String author;
    private String genre;
    private boolean availability;
}
