package co.demo.services;

import co.server.annotation.Param;

public interface UserService {
    public String getUser(@Param("id") int id, @Param("name") String name);
}
