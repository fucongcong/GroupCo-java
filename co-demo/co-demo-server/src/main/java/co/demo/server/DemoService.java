package co.demo.server;

import co.server.annotation.Param;

public interface DemoService {
    String sayHello(@Param("name") String name);
}
