package com.whedc.serviceImpl;

import com.whedc.model.User;
import com.whedc.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("user.getName= " + user.getName());
        return user;
    }
}
