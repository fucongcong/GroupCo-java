package co.shop.service;

import co.server.annotation.Param;
import co.shop.api.ShopService;
import co.shop.dao.Entity.ShopEntity;
import co.shop.dao.Repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Service("shopService")
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepository shopRepository;

    public ShopEntity getShop(@Param("id") Integer id) {
        return shopRepository.findById(id).get();
    }

    public ShopEntity addShop(@Param("data") ShopEntity data) {
        return shopRepository.save(data);
    }

    public ShopEntity editShop(@Param("id") Integer id, @Param("data") ShopEntity data) {
        data.setId(id);
        return shopRepository.save(data);
    }

    public Boolean deleteShop(@Param("id") Integer id) {
        shopRepository.deleteById(id);
        return true;
    }

    public Iterable<ShopEntity> searchShop(@Param("conditions") Map conditions,
                                           @Param("orderBy") List orderBy, @Param("start") Integer start,
                                           @Param("limit") Integer limit) {
        Specification specification = new Specification<ShopEntity>() {
            public Predicate toPredicate(Root<ShopEntity> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {

                if (conditions.containsKey("userId")) {
                    return builder.equal(root.get("userId").as(Integer.class), conditions.get("userId"));
                }

                return null;
            }
        };
        return shopRepository.findAll(specification, new QPageRequest(start, limit));
    }

    public Iterable<ShopEntity> searchShopCount(@Param("conditions") Map conditions) {
        return null;
    }

    public ShopEntity getUserShopByUserId(@Param("userId") Integer userId) {
        return shopRepository.getShopByUserId(userId);
    }

    public Iterable<ShopEntity> getAllUserShop() {
        return shopRepository.findAll();
    }
}
