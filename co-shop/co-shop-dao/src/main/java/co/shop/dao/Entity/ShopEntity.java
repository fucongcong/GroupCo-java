package co.shop.dao.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_shop")
public class ShopEntity implements Serializable {

    private int id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String name;

    private String code;

    private String address;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMcodes() {
        return mcodes;
    }

    public void setMcodes(String mcodes) {
        this.mcodes = mcodes;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public Integer getBodydata() {
        return bodydata;
    }

    public void setBodydata(Integer bodydata) {
        this.bodydata = bodydata;
    }

    private String tel;

    private String mcodes;

    private Integer userId;

    private Integer orders;

    private Integer bodydata;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
