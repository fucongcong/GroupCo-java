### A Simple Rpc Server For Group-Co client

### How it works

#### Interface
```java
    package Service;

    public interface UserService {

        public String getUser(int id, String name);
    }

```
#### 服务端提供Service实例

```java

    package Service.Impl;

    import Core.Param;
    import Service.UserService;

    public class UserServiceImpl implements UserService {

        public String getUser(@Param("id") int id, @Param("name") String name) {
            return "user_"+id+"_"+name;
        }
    }

```

#### 启动Server

#### 使用Group-Co框架的Tcp客户端调用

##### 注意设置config/app.php中
```php
        'protocol' => 'buf',
        //包体的打包方式json,serialize
        'pack' => 'json',
        //是否启用gzip压缩true,false
        'gzip' => false,
```

##### 调用

```php
    $tcp = new AsyncTcp('127.0.0.1', 9394);
    $res = (yield $tcp->call(['cmd' => 'User::getUser', 'data' => ['id' => 1, 'name' => "coco"]]));

    //it will return
    //"{"cmd":"User::getUser","data":"user_1_coco"}"
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
