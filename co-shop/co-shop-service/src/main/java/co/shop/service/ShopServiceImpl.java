package co.shop.service;

import co.server.annotation.Param;
import co.shop.api.ShopService;
import co.shop.dao.Entity.ShopEntity;
import co.shop.dao.Repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("shopService")
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepository shopRepository;

    @Override
    public String test(@Param("name") String name) {
        System.out.println("name = " + name);
        return name;
    }

    public ShopEntity getShop(@Param("name") String name) {
        System.out.println("11name = " + name);return shopRepository.getShopByName(name);
    }
}
