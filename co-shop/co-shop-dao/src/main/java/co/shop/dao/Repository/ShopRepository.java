package co.shop.dao.Repository;


import co.shop.dao.Entity.ShopEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends CrudRepository<ShopEntity, Integer> {
    @Query("select u from ShopEntity u where u.userId = ?1")
    ShopEntity getShopByUserId(Integer userId);

    Page<ShopEntity> findAll(Specification<ShopEntity> spec, Pageable pageable);
}