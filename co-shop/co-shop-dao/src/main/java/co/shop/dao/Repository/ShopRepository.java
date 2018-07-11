package co.shop.dao.Repository;


import co.shop.dao.Entity.ShopEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends CrudRepository<ShopEntity, Integer> {
    @Query("select u from ShopEntity u where u.name = ?1")
    ShopEntity getShopByName(String name);
}