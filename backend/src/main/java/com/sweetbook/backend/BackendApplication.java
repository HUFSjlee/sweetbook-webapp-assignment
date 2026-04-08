package com.sweetbook.backend;

import com.sweetbook.backend.config.SupabaseProperties;
import com.sweetbook.backend.config.SweetBookProperties;
import com.sweetbook.backend.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        SweetBookProperties.class,
        SupabaseProperties.class,
        StorageProperties.class
})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
