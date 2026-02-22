package org.ostad.ostad_assignment_25;

import org.ostad.ostad_assignment_25.config.GithubProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(GithubProperties.class)
public class OstadAssignment25Application {

    public static void main(String[] args) {
        SpringApplication.run(OstadAssignment25Application.class, args);
    }

}
