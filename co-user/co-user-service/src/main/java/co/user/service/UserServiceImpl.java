package co.user.service;


import co.server.proxy.ProxyFactory;
import co.shop.api.ShopService;
import co.shop.dao.Entity.ShopEntity;
import co.user.api.UserService;
import co.user.dao.Entity.UserEntity;

import co.server.Services;
import co.server.annotation.Param;

import co.user.dao.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Proxy;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public UserEntity getUser(@Param("id") Integer id) {
        try {
            ShopService shopService = Services.getBean("shopService");
            System.out.println("shopEntity = " + shopService.test("aaa"));
            ShopEntity shopEntity = shopService.getShop("aaa");
            System.out.println("shopEntity.getId() = " + shopEntity.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userRepository.findById(id).get();
    }

    public Integer addUser(@Param("user") UserEntity user) {
        return userRepository.save(user).getId();
    }

    public UserEntity getUserByMobile(@Param("mobile") String mobile) {
        return userRepository.getUserByMobile(mobile);
    }
}