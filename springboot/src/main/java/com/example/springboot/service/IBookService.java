package com.example.springboot.service;

import com.example.springboot.controller.request.BookPageRequest;
import com.example.springboot.controller.request.CategoryPageRequest;
import com.example.springboot.entity.Book;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IBookService {
    List<Book> list();

    PageInfo<Book> page(BookPageRequest pageRequest);

    void save(Book obj);

    Book getById(Integer id);

    void update(Book obj);

    void deleteById(Integer id);

    /**
     * 基于用户借阅历史推荐图书
     * @param userId 用户ID
     * @return 推荐图书列表
     */
    List<Book> recommendBooks(Integer userId);

    /**
     * 按分类分页获取图书列表
     * @param category 分类名称（可选，为空则查询全部）
     * @param keyword 搜索关键字（ISBN或书名，可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页图书列表
     */
    PageInfo<Book> pageByCategory(String category, String keyword, Integer pageNum, Integer pageSize);
}
