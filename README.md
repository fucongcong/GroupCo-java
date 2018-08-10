### GroupCo-java

### 特性
- 支持内部服务调用
- 支持GroupCo的客户端调用
- 支持redis注册中心、服务的发现与注册
- 客户端缓存
- 服务优雅停机
- 故障切换(todo)

### 服务提供方
#### 定义服务接口
DemoService.java

```java
package co.demo.server;

import co.server.annotation.Param;

public interface DemoService {
    String sayHello(@Param("name") String name);
}

```

#### 在服务提供方实现接口
DemoServiceImpl
```java
package co.demo.server;

import co.server.annotation.Param;
import org.springframework.stereotype.Service;

@Service("demoService")
public class DemoServiceImpl implements DemoService {
    public String sayHello(@Param("name") String name) {
        return "Hello " + name;
    }
}
```

#### 用 Spring 配置声明暴露服务
app.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:redisson="http://redisson.org/schema/redisson"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://redisson.org/schema/redisson http://redisson.org/schema/redisson/redisson.xsd">

    <context:component-scan base-package="co.demo.server"/>
    <context:component-scan base-package="co.server"/>
    <bean id="ApplicationContextUtil" class="co.server.context.ApplicationContextUtil"></bean>

    <bean id="groupCoServer" class="co.server.CoServer">
        <property name="serviceName" value="Demo"/>
        <property name="port" value="8099"/>
        <!--公开的服务-->
        <property name="services">
            <map>
                <entry key="demoService">
                    <map>
                        <entry key="interface" value="co.demo.server.DemoService"/>
                    </map>
                </entry>
            </map>
        </property>
    </bean>
    
    <!--以redis作为注册中心-->
    <redisson:client id="coRedissonClient" codec-ref="codec">
        <redisson:single-server address="redis://127.0.0.1:6379"/>
    </redisson:client>
    <bean id="codec" class="org.redisson.client.codec.StringCodec"/>
    <bean id="redisKeyUtil"
          class="co.server.common.util.RedisKeyUtil">
        <property name="prefix" value="demo"></property>
    </bean>
</beans>
```
#### 加载 Spring 配置
ServiceProvider.java
```java
import co.server.CoServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceProvider {

    public static void main(String[] args) throws Exception {
        System.out.println("service starting...");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:app.xml");
        context.start();
    }
}
```
### 服务消费者
#### 通过 Spring 配置引用远程服务
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:redisson="http://redisson.org/schema/redisson"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://redisson.org/schema/redisson http://redisson.org/schema/redisson/redisson.xsd">

    <context:component-scan base-package="co.server"/>
    <bean id="ApplicationContextUtil" class="co.server.context.ApplicationContextUtil"></bean>

    <bean id="groupCoServer" class="co.server.CoServer">
        <property name="serviceName" value="DemoClient"/>
        <property name="port" value="8098"/>
        <!--依赖的服务-->
        <property name="references">
            <map>
                <entry key="demoService">
                    <map>
                        <entry key="serverName" value="Demo"></entry>
                        <entry key="interface" value="co.demo.server.DemoService"/>
                    </map>
                </entry>
            </map>
        </property>
    </bean>

    <redisson:client id="coRedissonClient" codec-ref="codec">
        <redisson:single-server address="redis://127.0.0.1:6379"/>
    </redisson:client>
    <bean id="codec" class="org.redisson.client.codec.StringCodec"/>
    <bean id="redisKeyUtil"
          class="co.server.common.util.RedisKeyUtil">
        <property name="prefix" value="demo"></property>
    </bean>
</beans>
```
#### 加载Spring配置，并调用远程服务
Consumer.java
```java
import co.demo.server.DemoService;
import co.server.CoServer;
import co.server.Services;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:app.xml");
        context.start();

        DemoService demoService = Services.getBean("demoService");
        String hello = demoService.sayHello("world"); // 执行远程方法
        System.out.println( hello ); // 显示调用结果
    }
}

```
### PHP调用

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
    $service = (yield service_center('Demo'));
    $res = (yield $service->call("Demo::sayHello", ['name' => world]));
    dump($res);
```

##### 使用异步TCP客户端调用

```php
    $tcp = new AsyncTcp('127.0.0.1', 8099);
    $res = (yield $tcp->call(['cmd' => 'Demo\\Demo::sayHello', 'data' => ['name' => 'world']]));

    //it will return
    //{"cmd":"Demo\\Demo::sayHello","code":200,"data":"Hello world","type":"json","version":"1.0.0"}
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
        $cmd   = "Demo\\Demo::sayHello";
        $data   = array('name' => 'world');
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

    $client->connect('127.0.0.1', 8099, 0.5);
```
