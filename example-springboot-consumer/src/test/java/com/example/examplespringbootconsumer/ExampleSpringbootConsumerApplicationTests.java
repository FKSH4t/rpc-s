package com.example.examplespringbootconsumer;

import com.example.examplespringbootconsumer.service.UserServiceReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ExampleSpringbootConsumerApplicationTests {
    @Resource
    private UserServiceReference userService;

    @Test
    void contextLoads() {
    }

    @Test
    void test1() {
        userService.test();
    }

}
