package com.FeedEmGreens.HealthyAura.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for handling Cross-Origin Resource Sharing (CORS) settings in the application.
 *
 * <p>This class implements {@link WebMvcConfigurer} to customize CORS mappings,
 * allowing specific origins, HTTP methods, and headers to access the backend API.</p>
 *
 * <p>By default, this configuration enables requests from the frontend (http://localhost:3000)
 * — typically a React application during local development — and allows common RESTful
 * HTTP methods such as GET, POST, PUT, DELETE, and OPTIONS.</p>
 *
 * <p><strong>Note:</strong> This configuration should be adjusted or restricted in production
 * environments to enhance security.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer{

    /**
     * Configures global CORS mappings for the application.
     *
     * <p>This method allows cross-origin requests from the frontend server running
     * on <code>http://localhost:3000</code> to the backend API. It also enables credentials
     * (e.g., cookies or authorization headers) to be sent with requests.</p>
     *
     * @param registry the {@link CorsRegistry} used to define allowed origins, methods, and headers
     */

    public void addCorsMapping(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}
