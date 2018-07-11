package co.demo.server;

import co.server.annotation.Param;
import org.springframework.stereotype.Service;

@Service("demoService")
public class DemoServiceImpl implements DemoService {
    public String sayHello(@Param("name") String name) {
        return "Hello " + name;
    }
}