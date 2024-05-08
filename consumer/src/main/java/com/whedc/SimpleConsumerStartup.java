package com.whedc;

import com.whedc.bootstrap.ConsumerBootstrap;
import com.whedc.model.User;
import com.whedc.proxy.ServiceProxyFactory;
import com.whedc.service.ItemService;
import com.whedc.service.UserService;

public class SimpleConsumerStartup {
    public static void main(String[] args) {
        // 消费者初始化
        ConsumerBootstrap.init();
        // 消费者获取服务
        User user = new User();
        UserService userService = ServiceProxyFactory.getProxyInstance(UserService.class);
        ItemService itemService = ServiceProxyFactory.getProxyInstance(ItemService.class);
        user.setName("UserServiceName");
        User getUserFromRemote = userService.getUser(user);
//        userService.getUser(user);
//        itemService.testService();
//        userService.getUser(user);
        if (getUserFromRemote != null) {
            System.out.println(getUserFromRemote.getName());
        } else {
            System.out.println("user == null");
        }
//        System.out.println("userService.getMockShort() = " + userService.getMockShort());
    }
}
