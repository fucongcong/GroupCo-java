import co.server.CoServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceProvider {

    private static final Logger logger = LogManager.getLogger(ServiceProvider.class);

    public static void main(String[] args) throws Exception {
        logger.info("service starting...");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
        context.start();
    }
}