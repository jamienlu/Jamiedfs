package io.github.jamielu.jamiedfs.conf;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jamieLu
 * @create 2024-07-23
 */
@Configuration
public class DfsConfig {
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation("/tmp/tomcat");
        return factory.createMultipartConfig();
    }
}
