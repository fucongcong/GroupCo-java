package co.shop.api;

import co.server.annotation.Param;
import co.shop.dao.Entity.ShopEntity;

import java.util.List;
import java.util.Map;

public interface ShopService {
    public ShopEntity getShop(@Param("id") Integer id);

    public ShopEntity addShop(@Param("data") ShopEntity data);

    public ShopEntity editShop(@Param("id") Integer id, @Param("data") ShopEntity data);

    public Boolean deleteShop(@Param("id") Integer id);

    public Iterable<ShopEntity> searchShop(@Param("conditions") Map conditions, @Param("orderBy") List orderBy, @Param("start") Integer start, @Param("limit") Integer limit);

    public Iterable<ShopEntity> searchShopCount(@Param("conditions") Map conditions);

    public ShopEntity getUserShopByUserId(@Param("userId") Integer userId);

    public Iterable<ShopEntity> getAllUserShop();
}
