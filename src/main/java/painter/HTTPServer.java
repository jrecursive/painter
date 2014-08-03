/*
 * Painter
 * Copyright (c) 2014, John Muellerleile, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 *
*/
package painter;

import java.net.*;
import java.util.logging.*;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HTTPServer {
    final private static Logger log = 
        Logger.getLogger(HTTPServer.class.getName());
    final private String addr = "0.0.0.0";
    final private Server server;
    final private ServletContextHandler context;
    final private Thread webServerThread;
    final private String wwwRoot = "./www";
    
    public HTTPServer() throws Exception {
        log.info("starting jetty");
        server = new Server(new InetSocketAddress(addr, Painter.port));
        context = new ServletContextHandler(ServletContextHandler.NO_SECURITY);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder holder = 
            context.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class, "/*");
        holder.setInitParameter("resourceBase", wwwRoot);
        holder.setInitParameter("pathInfoOnly", "true");
        webServerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    server.start();
                } catch (Exception ex) {
                    log.info("cannot start jetty");
                    ex.printStackTrace();
                    System.exit(-1);
                }
            }
        });
        webServerThread.start();
        log.info("jetty started addr " + addr + " port " + Painter.port);
    }
}
