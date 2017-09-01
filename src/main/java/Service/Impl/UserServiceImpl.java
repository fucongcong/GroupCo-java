package Service.Impl;

import Core.annotation.Param;
import Service.UserService;

public class UserServiceImpl implements UserService {

    public String getUser(@Param("id") int id, @Param("name") String name) {
        return "user_"+id+"_"+name;
    }
}


