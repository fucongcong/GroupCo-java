package co.shop.api;

import co.server.annotation.Param;
import co.shop.dao.Entity.ShopEntity;

public interface ShopService {
    public ShopEntity getShop(@Param("name") String name);

    public String test(@Param("name") String name);
}
