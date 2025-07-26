package com.example;

import com.example.resource.CitizenResource;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;

@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    
    public JerseyConfig() {
        // Register REST resources
        register(CitizenResource.class);
        
        // Enable validation
        register(ValidationFeature.class);
        
        // Enable JSON support
        //register(org.glassfish.jersey.media.json.jackson.JacksonFeature.class);
    }
}