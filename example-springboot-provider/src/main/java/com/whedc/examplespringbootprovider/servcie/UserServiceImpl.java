package com.whedc.examplespringbootprovider.servcie;

import com.whedc.model.User;
import com.whedc.rpcsspringbootstarter.annotation.RpcService;
import com.whedc.service.UserService;
import org.springframework.stereotype.Service;

@RpcService
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
