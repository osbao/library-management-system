package com.example.springboot.mapper;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.BookPageRequest;
import com.example.springboot.entity.Book;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {

    List<Book> list();

    List<Book> listByCondition(BookPageRequest pageRequest);

    void save(Book obj);

    Book getById(Integer id);

    void updateById(Book obj);

    void deleteById(Integer id);

    Book getByNo(String bookNo);

    Book getByBookNo(String bookNo);

    Book getByName(String name);

    void updateNumByNo(String bookNo);

    void updateNumByNoWithCount(@org.apache.ibatis.annotations.Param("bookNo") String bookNo, @org.apache.ibatis.annotations.Param("count") Integer count);

    /**
     * 按分类查询图书列表
     * @param category 分类名称（可选）
     * @param keyword 搜索关键字（ISBN或书名，可选）
     * @return 图书列表
     */
    List<Book> listByCategory(@org.apache.ibatis.annotations.Param("category") String category,
                              @org.apache.ibatis.annotations.Param("keyword") String keyword);

}
