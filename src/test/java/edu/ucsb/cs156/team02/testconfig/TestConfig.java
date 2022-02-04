package edu.ucsb.cs156.team02.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import edu.ucsb.cs156.team02.services.CurrentUserService;
import edu.ucsb.cs156.team02.services.GrantedAuthoritiesService;
import edu.ucsb.cs156.team02.services.LoggingService;

@TestConfiguration
public class TestConfig {

    @Bean
    public CurrentUserService currentUserService() {
        return new MockCurrentUserServiceImpl();
    }

    @Bean
    public GrantedAuthoritiesService grantedAuthoritiesService() {
        return new GrantedAuthoritiesService();
    }

    @Bean
    public LoggingService loggingService() {
        return new LoggingService();
    }

}