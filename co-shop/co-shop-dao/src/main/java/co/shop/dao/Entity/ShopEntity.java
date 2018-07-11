package co.shop.dao.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "shop", schema = "Demo")
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
