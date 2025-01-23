package io.github.jamielu.jamiedfs;

import io.github.jamielu.jamiedfs.conf.DfsConfigProperties;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static io.github.jamielu.jamiedfs.uitls.FileUtils.init;

@SpringBootApplication
@EnableConfigurationProperties(DfsConfigProperties.class)
@Import(RocketMQAutoConfiguration.class)
public class JamiedfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JamiedfsApplication.class, args);
    }

    @Value("${dfs.uploadPath}")
    private String uploadPath;

    @Bean
    ApplicationRunner runner() {
        return args -> {
            init(uploadPath);
            System.out.println("dfs app started");
        };
    }
}
