package com.example;

import com.example.dao.CitizenDAO;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {
    
    private static final int PORT = 8080;
    
    public static void main(String[] args) {
        Server server = new Server(PORT);
        
        // Create servlet context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        // Create Jersey servlet
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jakarta.ws.rs.Application", "com.example.JerseyConfig");
        
        server.setHandler(context);
        
        // Add shutdown hook to properly close database connections
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down server...");
                CitizenDAO.closeEntityManagerFactory();
                server.stop();
                System.out.println("Server stopped.");
            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }));
        
        try {
            System.out.println("Starting server on port " + PORT + "...");
            server.start();
            System.out.println("Server started successfully!");
            System.out.println("API available at: http://localhost:" + PORT + "/api/citizens");
            System.out.println("Press Ctrl+C to stop the server");
            server.join();
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}