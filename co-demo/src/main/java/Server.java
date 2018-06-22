
import co.server.co.CoServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

    public static void main(String[] args) throws Exception {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
        context.start();
        CoServer server = (CoServer) context.getBean("groupCoServer");
        server.run();
    }
}
