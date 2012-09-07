package com.etsy.pushbot.config;

import java.io.File;
import java.net.URL;

import com.etsy.pushbot.PushBot;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.servlet.ServletHolder;

public class ConfigServer {

    /**
     * @param args
     */
    public ConfigServer(PushBot pushBot) throws Exception {

        String webappDirLocation = "src/main/webapp/";

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        Server server = new Server(Integer.valueOf(webPort));
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setDescriptor(webappDirLocation+"/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        // Add the status servlet
        ServletHolder servletHolder = new ServletHolder(new StatusServlet(pushBot));
        root.addServlet(servletHolder, "/status");

        //Parent loader priority is a class loader setting that Jetty accepts.
        //By default Jetty will behave like most web containers in that it will
        //allow your application to replace non-server libraries that are part of the
        //container. Setting parent loader priority to true changes this behavior.
        //Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        root.setParentLoaderPriority(true);

        server.setHandler(root);

        server.start();
        server.join();
    }

}
