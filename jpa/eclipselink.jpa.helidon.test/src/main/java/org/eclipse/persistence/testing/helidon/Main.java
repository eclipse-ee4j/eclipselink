package org.eclipse.persistence.testing.helidon;

import io.helidon.microprofile.server.Server;

public class Main {

        public static void main(final String[] args) {
        Server server = startServer();
        // logger.warn("Started server on http://{}:{}", server.host(), server.port());
    }

    public static Server startServer() {
        // Server will automatically pick up configuration from mp-meta-config.yaml
        // and Application classes annotated as @ApplicationScoped
        return Server.builder().retainDiscoveredApplications(true).
                build().
                start();
    }

}
