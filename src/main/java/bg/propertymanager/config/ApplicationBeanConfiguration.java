package bg.propertymanager.config;

import bg.propertymanager.service.TaxService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}