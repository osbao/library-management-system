package com.example.springboot.service.impl;

import com.example.springboot.entity.BookItem;
import com.example.springboot.mapper.BookItemMapper;
import com.example.springboot.service.IBookItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookItemServiceImpl implements IBookItemService {
    
    @Autowired
    private BookItemMapper bookItemMapper;
    
    @Override
    public List<BookItem> listByIsbn(String isbn) {
        try {
            return bookItemMapper.listByIsbn(isbn);
        } catch (Exception e) {
            log.error("查询馆藏记录失败，isbn: {}", isbn, e);
            throw new RuntimeException("查询馆藏记录失败", e);
        }
    }
    
    @Override
    public List<BookItem> listByBookId(Integer bookId) {
        try {
            return bookItemMapper.listByBookId(bookId);
        } catch (Exception e) {
            log.error("查询馆藏记录失败，bookId: {}", bookId, e);
            throw new RuntimeException("查询馆藏记录失败", e);
        }
    }
}
