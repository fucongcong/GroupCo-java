package co.demo.services.Dao;

import co.demo.services.Entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    UserEntity selectUser(@Param("id") int id);
}