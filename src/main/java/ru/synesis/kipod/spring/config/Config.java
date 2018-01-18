package ru.synesis.kipod.spring.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.IgniteSpringBean;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ImportResource({
    "classpath:ignite-discovery-multicast.xml",
    "classpath:ignite-common-entity.xml",
    "classpath:ignite-common-storage.xml",
    "classpath:ignite-common-cache-conf.xml",
    "classpath:ignite-common.xml",
    "classpath:ignite-streamer.xml",
})
@EnableScheduling
public class Config {
    
    @Bean("igniteServer1")
    public Ignite igniteServer1(@Qualifier("igniteSrv1") IgniteConfiguration igniteCfg, ApplicationContext applicationContext) throws IgniteCheckedException {
        return IgniteSpring.start(igniteCfg, applicationContext);
    }
    
    @Bean("igniteServer2")
    public Ignite igniteServer2(@Qualifier("igniteSrv2") IgniteConfiguration igniteCfg, ApplicationContext applicationContext) throws IgniteCheckedException {
        return IgniteSpring.start(igniteCfg, applicationContext);
    }

    @Bean("igniteClient")
    @DependsOn({"igniteServer1", "igniteServer2"})
    public Ignite ignite(@Qualifier("igniteClient1") IgniteConfiguration igniteCfg, ApplicationContext applicationContext) {
        IgniteSpringBean bean = new IgniteSpringBean();
        bean.setConfiguration(igniteCfg);
        bean.setApplicationContext(applicationContext);
        return bean;
    }
    
}
