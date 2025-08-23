package com.jamd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jamd.dao.ManagerDB;

@Configuration
public class AppConfig {
        @Bean
    public ManagerDB managerDB() {
        return new ManagerDB();
    }

}
