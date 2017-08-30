package Service;

import Core.Param;

public class UserService {

    public String getUser(@Param("id") int id,  @Param("name") String name) {
        return "user_"+id+"_"+name;
    }
}
