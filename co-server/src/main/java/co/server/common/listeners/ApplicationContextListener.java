package co.server.common.listeners;

import co.server.CoClient;
import co.server.CoServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Service;

@Service("contextStartedListener")
public class ApplicationContextListener implements ApplicationListener<ContextStartedEvent>{
    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        if (event.getApplicationContext().containsBean("groupCoServer")) {
            event.getApplicationContext().getBean("groupCoServer", CoServer.class).run();
        }

        if (event.getApplicationContext().containsBean("groupCoClient")) {
            event.getApplicationContext().getBean("groupCoClient", CoClient.class).run();
        }
    }
}
