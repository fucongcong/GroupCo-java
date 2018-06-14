import com.co.server.co.CoServer;
import com.co.server.co.Context;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Server {
    public static void main(String[] args) throws Exception {

        Config configLoader = ConfigFactory.load();
        Context ctx = new Context();
        ctx.singleton("config", configLoader);

        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = configLoader.getInt("port");;
        }

        new CoServer(port).run();
    }
}
