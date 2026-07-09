package com.example.springboot.mapper;

import com.example.springboot.entity.BookItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookItemMapper {
    
    /**
     * 根据图书ISBN查询所有馆藏记录
     * @param isbn ISBN号
     * @return 馆藏记录列表
     */
    List<BookItem> listByIsbn(@Param("isbn") String isbn);
    
    /**
     * 根据图书ID查询所有馆藏记录
     * @param bookId 图书ID
     * @return 馆藏记录列表
     */
    List<BookItem> listByBookId(@Param("bookId") Integer bookId);
    
    /**
     * 根据状态查询馆藏记录
     * @param status 状态
     * @return 馆藏记录列表
     */
    List<BookItem> listByStatus(@Param("status") String status);
    
    /**
     * 更新图书副本状态
     */
    void updateStatus(@Param("id") Long id, @Param("status") String status);
}
