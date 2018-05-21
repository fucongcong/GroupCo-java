import Core.NioServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RpcServer {

    public static void main(String[] args) {

        Config conf = ConfigFactory.load();
        int port = conf.getInt("port");
        System.out.print(port);

        NioServer server = new NioServer();
        server.run();
    }
}