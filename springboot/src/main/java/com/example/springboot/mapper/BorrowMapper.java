package com.example.springboot.mapper;
import com.example.springboot.controller.request.BorrowPageRequest;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Return_;
import com.example.springboot.pojo.dto.BooksSalesDTO;
import com.example.springboot.pojo.dto.UsersBorrowDTO;
import com.example.springboot.pojo.dto.CategorySalesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.springboot.mapper.po.BorrowReturCountPO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BorrowMapper {

    List<Borrow> list();

    List<Borrow> listByCondition(BorrowPageRequest pageRequest);

    List<Return_> listReturByCondition(BorrowPageRequest pageRequest);

    void save(Borrow obj);

    void saveRetur(Return_ obj);

    Borrow getById(Integer id);

    void updateById(Borrow obj);

    void deleteById(Integer id);

    void deleteReturById(Integer id);

    void updateStatus(@Param("status")String status,@Param("id")Integer id);

    List<BorrowReturCountPO> getCountByTimeRange(@Param("timeRange") String timeRange, @Param("type") int type);

    List<BooksSalesDTO> getSalesTop10(@Param("begin") LocalDateTime beginTime,@Param("end") LocalDateTime endTime);

    List<UsersBorrowDTO> getUsersTop10(@Param("begin") LocalDateTime beginTime,@Param("end") LocalDateTime endTime);

    List<CategorySalesDTO> getCategoryTop10(@Param("begin") LocalDateTime beginTime,@Param("end") LocalDateTime endTime);
    
    // 预约相关方法
    
    /**
     * 查询用户的预约记录
     */
    List<Borrow> listByUserNoAndStatus(@Param("userNo") String userNo, @Param("status") String status);
    
    /**
     * 查询超时的预约记录（待取书且超过过期时间）
     */
    List<Borrow> listExpiredReservations();
    
    /**
     * 查询管理员端的待取书列表
     */
    List<Borrow> listWaitingPickup();
    
    /**
     * 根据用户编号和图书编号查询预约记录
     */
    Borrow getByUserNoAndBookNo(@Param("userNo") String userNo, @Param("bookNo") String bookNo);
    
    /**
     * 获取指定图书的借阅统计数据（按月份）
     */
    List<BorrowReturCountPO> getBookBorrowStats(@Param("bookNo") String bookNo);
}
