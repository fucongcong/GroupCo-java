package co.demo.services;


import co.demo.services.Entity.UserEntity;
import co.server.annotation.Param;

public interface UserService {
    public UserEntity getUser(@Param("id") Integer id);

    public Integer addUser(@Param("user") UserEntity user);

    public UserEntity getUserByMobile(@Param("mobile") String mobile);
}
