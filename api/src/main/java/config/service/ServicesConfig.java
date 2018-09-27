package config.service;

import com.trizic.api.service.AdvisorService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = AdvisorService.class)
public class ServicesConfig {
}
