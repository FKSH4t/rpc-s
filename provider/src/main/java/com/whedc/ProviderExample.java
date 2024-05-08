package com.whedc;

import com.whedc.bootstrap.ProviderBootstrap;
import com.whedc.bootstrap.ServiceRegisterInfo;
import com.whedc.service.ItemService;
import com.whedc.service.UserService;
import com.whedc.serviceImpl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {
    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>(){
            {
                add(new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class));
                add(new ServiceRegisterInfo<>(ItemService.class.getName(), ItemService.class));
            }
        };
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
