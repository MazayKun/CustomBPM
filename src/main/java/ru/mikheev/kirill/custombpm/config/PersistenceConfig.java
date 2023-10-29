package ru.mikheev.kirill.custombpm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ru.mikheev.kirill.custombpm.repository")
public class PersistenceConfig {
}
