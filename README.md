### GroupCo基础服务提供者JAVA版

### 快速开始

#### 编写服务接口UserService
```java
package co.demo.services;


import co.demo.services.Entity.UserEntity;
import co.server.annotation.Param;

public interface UserService {
    public UserEntity getUser(@Param("id") Integer id);

    public Integer addUser(@Param("user") UserEntity user);

    public UserEntity getUserByMobile(@Param("mobile") String mobile);
}


```
#### 编写UserEntity实例

```java

package co.demo.services.Entity;

import javax.persistence.*;

@Entity
@Table(name = "user", schema = "Demo")
public class UserEntity {

    private int id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String mobile;
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

```

#### 编写Repository

```java
    package co.demo.services.Dao;
    
    import co.demo.services.Entity.UserEntity;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.CrudRepository;
    import org.springframework.stereotype.Repository;
    
    @Repository
    public interface UserRepository extends CrudRepository<UserEntity, Integer> {
        @Query("select u from UserEntity u where u.mobile = ?1")
        UserEntity getUserByMobile(String mobile);
    }
```


#### 实现UserServiceIml
```java

package co.demo.services.Impl;

import co.demo.services.Dao.UserRepository;
import co.demo.services.Entity.UserEntity;
import co.demo.services.UserService;
import co.server.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public UserEntity getUser(@Param("id") Integer id) {
        return userRepository.findById(id).get();
    }

    public Integer addUser(@Param("user") UserEntity user) {
        return userRepository.save(user).getId();
    }

    public UserEntity getUserByMobile(@Param("mobile") String mobile) {
        return userRepository.getUserByMobile(mobile);
    }
}

```

#### 启动ServiceProvider。

#### 使用Group-Co框架的Tcp客户端调用

##### 注意设置config/app.php中
```php
        'protocol' => 'buf',
        //包体的打包方式json,serialize
        'pack' => 'json',
        //是否启用gzip压缩true,false
        'gzip' => false,
```

##### 使用service_center()服务中心调用
```php
    $service = (yield service_center('User'));
    $user = (yield $service->call("User::getUser", ['id' => $userId]));
    dump($user);
```

##### 使用异步TCP客户端调用

```php
    $tcp = new AsyncTcp('127.0.0.1', 8087);
    $res = (yield $tcp->call(['cmd' => 'User\\User::getUser', 'data' => ['id' => 1]]));

    //it will return
    //{"cmd":"User\\User::getUser","data":{"id":1,"mobile":"18768176261","password":"11111"}}
```

#### 使用swoole客户端调用

```php
    <?php

    $client = new Swoole\Client(SWOOLE_SOCK_TCP, SWOOLE_SOCK_ASYNC);
    $client->set(array(
        'open_length_check' => true,
        'package_length_type' => 'N',
        'package_max_length' => 2000000,
        'package_length_offset' => 0,
        'package_body_offset'   => 4,
    ));

    $client->on("connect", function($cli) {
        $cmd   = "User::getUser";
        $data   = array('id' => 123, 'name' => 'cococ');
        $bin_body   = pack("a*", json_encode(['cmd' => $cmd, 'data' => $data]));
        $body_len   = strlen($bin_body);
        $bin_head   = pack("N", $body_len);
        $bin_data   = $bin_head . $bin_body;
        $cli->send($bin_data);
    });
    $client->on("receive", function($cli, $data){
        $data = substr($data, 4);
        echo "Received: ".$data."\n";
    });
    $client->on("error", function($cli){
        echo "Connect failed\n";
    });
    $client->on("close", function($cli){
        echo "Connection close\n";
    });

    $client->connect('127.0.0.1', 9394, 0.5);
```
