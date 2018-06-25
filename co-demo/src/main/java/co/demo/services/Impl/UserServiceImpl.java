package co.demo.services.Impl;

import co.demo.services.Entity.UserEntity;
import co.demo.services.Dao.UserMapper;
import co.demo.services.UserService;
import co.server.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl  implements UserService {

    @Autowired
    private UserMapper userMapper;

    public String getUser(@Param("id") int id, @Param("name") String name) {
        UserEntity user = userMapper.selectUser(id);
        return user.getMobile();
    }
}