package com.example.springboot.service;

import com.example.springboot.entity.BookItem;

import java.util.List;

public interface IBookItemService {
    
    /**
     * 根据图书ISBN查询所有馆藏记录
     * @param isbn ISBN号
     * @return 馆藏记录列表
     */
    List<BookItem> listByIsbn(String isbn);
    
    /**
     * 根据图书ID查询所有馆藏记录
     * @param bookId 图书ID
     * @return 馆藏记录列表
     */
    List<BookItem> listByBookId(Integer bookId);
}
