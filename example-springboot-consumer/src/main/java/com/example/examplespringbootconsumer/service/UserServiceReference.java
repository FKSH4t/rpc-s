package com.example.examplespringbootconsumer.service;

import com.whedc.model.User;
import com.whedc.rpcsspringbootstarter.annotation.RpcReference;
import com.whedc.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceReference {
    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User("whedc-rpc-s");
        User userServiceUser = userService.getUser(user);
        System.out.println(userServiceUser);
    }
}
