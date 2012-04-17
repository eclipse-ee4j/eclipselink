/**
 * 
 */
package org.eclipse.persistence.jpars.test.http;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

/**
 * @author dclarke
 * 
 */
public class BaseHttpServerTest {

    @Test
    public void test() {

    }

    private static HttpServer server;

    @BeforeClass
    public static void startServer() throws IllegalArgumentException, IOException {
        HttpServer s = HttpServerFactory.create("http://localhost:8080/avatar/");
        s.start();
        server = s;
    }

    @AfterClass
    public static void stopServer() {
       server.stop(0);
    }

}
