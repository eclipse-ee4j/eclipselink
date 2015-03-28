package _dbws;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ProviderListener implements ServletContextListener {

    public static ServletContext SC = null;

    public  ProviderListener() {
        super();
    }

    public void contextInitialized(ServletContextEvent sce) {
        SC = sce.getServletContext();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // no-op
    }
}
