package co.user.api;


import co.server.annotation.Param;
import co.user.dao.Entity.UserEntity;

public interface UserService {
    public co.user.dao.Entity.UserEntity getUser(@Param("id") Integer id);

    public Integer addUser(@Param("user") UserEntity user);

    public co.user.dao.Entity.UserEntity getUserByMobile(@Param("mobile") String mobile);
}
