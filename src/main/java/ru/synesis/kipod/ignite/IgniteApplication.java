package ru.synesis.kipod.ignite;

import javax.sql.DataSource;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteSpringBean;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class IgniteApplication {

    public static void main(String[] args) {
        SpringApplication.run(IgniteApplication.class, args);
    }
    
    @Configuration
    @ImportResource({
        "classpath:ignite-persistent-context.xml"
    })
    @EnableConfigurationProperties
    public static class Config {
        
        @Bean
        public Ignite ignite(IgniteConfiguration igniteCfg, ApplicationContext applicationContext) {
            IgniteSpringBean bean = new IgniteSpringBean();
            bean.setConfiguration(igniteCfg);
            bean.setApplicationContext(applicationContext);
            return bean;
        }
        
        @Bean
        @ConfigurationProperties("ignite.datasource")
        public DataSource igniteDataSource() {
            return new DriverManagerDataSource();
        }
        
        @Bean
        public JdbcTemplate igniteJdbcTemplate(DataSource igniteDataSource) {
            return new JdbcTemplate(igniteDataSource);
        }
        
    }
    
}
