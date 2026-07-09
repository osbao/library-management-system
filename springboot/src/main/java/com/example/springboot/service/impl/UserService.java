package com.example.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.SignRequest;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IUserService;
import com.example.springboot.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class UserService implements IUserService {
    @Autowired
    UserMapper userMapper;

    private static final String DEFAULT_PASS="1234";
    private static final String PASS_SALT="wq";
    @Override
    public List<User> list(){
        return userMapper.list();
    }

    @Override
    public PageInfo<User> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(),baseRequest.getPageSize());
        List<User> users=userMapper.listByCondition(baseRequest);
        return new PageInfo<>(users);
    }

    @Override
    public void save(User user) {
        Date date=new Date();
        user.setUsername(DateUtil.format(date,"yyyyMMdd") + Math.abs(IdUtil.fastSimpleUUID().hashCode()));
        // 设置默认密码
        if(user.getPassword() == null || user.getPassword().isEmpty()){
            user.setPassword(securePass(DEFAULT_PASS));
        } else {
            user.setPassword(securePass(user.getPassword()));
        }
        // 设置默认账户金额为0
        if(user.getAccount() == null){
            user.setAccount(0);
        }
        userMapper.save(user);
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    public void update(User user) {
        log.info("更新用户信息: id={}, name={}, age={}, sex={}", user.getId(), user.getName(), user.getAge(), user.getSex());
        user.setUpdatetime(new Date());
        userMapper.updateById(user);
        log.info("用户信息更新成功");
    }

    @Override
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    @Override
    public void handleAccount(User user){
        Integer score=user.getScore();
        if(score==null){
            return;
        }
        Integer id = user.getId();
        User user1=userMapper.getById(id);
        user.setAccount(user1.getAccount()+score);
        userMapper.updateById(user);

    }

    /**
     * 用户登录
     * @param request
     * @return
     */
    @Override
    public LoginDTO login(LoginRequest request) {
        User user = null;
        try{
            // 通过姓名查询用户
            user = userMapper.getByName(request.getUsername());
        }catch(Exception e){
            log.error("根据姓名{}查询出错",request.getUsername());
            throw new ServiceException("用户名错误");
        }
        //判断用户是否存在
        if(user == null){
            throw new ServiceException("用户名或密码错误");
        }
        //判断密码是否合法
        String securePass = securePass(request.getPassword());
        if(!securePass.equals(user.getPassword())){
            throw  new ServiceException("用户名或密码错误");
        }
        if(!user.isStatus()){
            throw new ServiceException("当前用户处于禁用状态");
        }
        LoginDTO loginDTO=new LoginDTO();
        BeanUtils.copyProperties(user,loginDTO);

        String token = TokenUtils.genToken(String.valueOf(user.getId()));

        loginDTO.setToken(token);
        return loginDTO;
    }

    /**
     * md5加密算法
     * @param password
     * @return
     */
    private String securePass(String password){
        return SecureUtil.md5(password);
    }

    /**
     * 修改密码
     * @param request
     */
    public void changePass(com.example.springboot.controller.request.PasswordRequest request) {
        // 验证旧密码
        User user = userMapper.getByUsername(request.getUsername());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        
        String oldPassSecure = securePass(request.getPassword());
        if (!oldPassSecure.equals(user.getPassword())) {
            throw new ServiceException("旧密码错误");
        }
        
        // 更新新密码
        String newPassSecure = securePass(request.getNewPass());
        user.setPassword(newPassSecure);
        user.setUpdatetime(new Date());
        userMapper.updateById(user);
    }

    /**
     * 用户注册
     * @param request
     */
    @Override
    public void sign(SignRequest request) {
        // 1. 判断密码和确认密码是否一致
        if (!request.getPassword().equals(request.getNewPassword())) {
            throw new ServiceException("密码和确认密码不一致");
        }
        
        // 2. 自动生成用户卡号（username）
        Date date = new Date();
        String generatedUsername = DateUtil.format(date, "yyyyMMdd") + Math.abs(IdUtil.fastSimpleUUID().hashCode());
        
        // 3. 加密密码
        String encryptedPassword = securePass(request.getPassword());
        
        // 4. 创建用户对象并保存（新用户注册赠送100积分）
        User user = new User();
        user.setName(request.getName());
        user.setUsername(generatedUsername);
        user.setPassword(encryptedPassword);
        user.setAccount(100);  // 设置默认账户金额为100积分（新用户注册赠送）
        
        try {
            userMapper.save(user);
            log.info("用户注册成功，用户名：{}，赠送100积分", generatedUsername);
        } catch (DuplicateKeyException e) {
            log.error("用户注册失败, username={}", generatedUsername, e);
            throw new ServiceException("用户名已存在");
        }
    }
}
