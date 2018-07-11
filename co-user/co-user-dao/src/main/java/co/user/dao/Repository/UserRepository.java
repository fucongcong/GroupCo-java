package co.user.dao.Repository;

import co.user.dao.Entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    @Query("select u from UserEntity u where u.mobile = ?1")
    UserEntity getUserByMobile(String mobile);
}