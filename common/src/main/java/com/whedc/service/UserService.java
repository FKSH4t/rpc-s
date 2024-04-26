package com.whedc.service;

import com.whedc.model.User;

public interface UserService {

    /**
     * 定义方法接口，获取用户，真实的实现在服务提供者模块中实现
     * @param user
     * @return
     */
    User getUser(User user);
}
