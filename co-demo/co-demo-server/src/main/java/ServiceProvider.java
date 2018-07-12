import co.server.CoServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceProvider {

    public static void main(String[] args) throws Exception {
        System.out.println("service starting...");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:app.xml");
        context.start();
    }
}