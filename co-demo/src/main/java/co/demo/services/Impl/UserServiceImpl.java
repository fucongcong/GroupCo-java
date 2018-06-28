package co.demo.services.Impl;

import co.demo.services.Dao.UserRepository;
import co.demo.services.Entity.UserEntity;
import co.demo.services.UserService;
import co.server.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public UserEntity getUser(@Param("id") Integer id) {
        return userRepository.findById(id).get();
    }

    public Integer addUser(@Param("user") UserEntity user) {
        return userRepository.save(user).getId();
    }

    public UserEntity getUserByMobile(@Param("mobile") String mobile) {
        return userRepository.getUserByMobile(mobile);
    }
}