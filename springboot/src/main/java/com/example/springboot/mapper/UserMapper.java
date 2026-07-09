package com.example.springboot.mapper;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.SignRequest;
import com.example.springboot.controller.request.UserPageRequest;
import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    //@Select("select * from user")
    List<User> list();

    List<User> listByCondition(BaseRequest baseRequest);

    void save(User user);

    User getById(Integer id);

    void updateById(User user);

    void deleteById(Integer id);

    User getByUsername(String userNo);

    User getByName(String name);

    void sign(SignRequest request);

    /**
     * 更新用户积分（增加或减少）
     * @param userNo 用户编号
     * @param score 积分数（正数表示增加，负数表示减少）
     */
    void updateScore(@Param("userNo") String userNo, @Param("score") Integer score);

//    void updateByUserNo(@Param("score")int score,@Param("status")String status, @Param("userNo") String userNo);
}
