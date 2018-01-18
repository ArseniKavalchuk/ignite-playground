package ru.synesis.kipod.test;

import java.io.File;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import com.google.common.io.Files;

import ru.synesis.kipod.event.EventConstants;
import ru.synesis.kipod.event.KipodEvent;
import ru.synesis.kipod.service.CacheReadyEvent;

//@Configuration
@PropertySource("classpath:application.properties")
@ImportResource({
    "classpath:ignite-discovery-multicast.xml",
    "classpath:ignite-common-entity.xml",
    "classpath:ignite-common-storage.xml",
    "classpath:ignite-common-cache-conf.xml",
    "classpath:ignite-common.xml",
    "classpath:ignite-streamer.xml",
})
public class IgniteTestConfiguration {

    @Bean
    public File workDir() {
        return Files.createTempDir();
    }
    
    @Bean("igniteServer1")
    public Ignite igniteServer1(
            @Qualifier("igniteSrv1") IgniteConfiguration igniteCfg,
            ApplicationContext applicationContext, 
            File workDir)
                    throws IgniteCheckedException {
        igniteCfg.setWorkDirectory(workDir.getAbsolutePath() + "/ignite-srv1");
        Ignite ignite = IgniteSpring.start(igniteCfg, applicationContext);
        return ignite;
    }
    
    @Bean("igniteServer2")
    public Ignite igniteServer2(
            @Qualifier("igniteSrv2") IgniteConfiguration igniteCfg,
            ApplicationContext applicationContext,
            File workDir) 
                    throws IgniteCheckedException {
        igniteCfg.setWorkDirectory(workDir.getAbsolutePath() + "/ignite-srv2");
        Ignite ignite = IgniteSpring.start(igniteCfg, applicationContext);
        return ignite;
    }

    @Bean("igniteClient")
    @DependsOn({"igniteServer1", "igniteServer2"})
    public Ignite ignite(
            @Qualifier("igniteClient1") IgniteConfiguration igniteCfg,
            @Qualifier("kipodEventCacheConfig") CacheConfiguration<String, KipodEvent> cacheConfig,
            ApplicationContext applicationContext,
            ApplicationEventPublisher publisher)
                    throws IgniteCheckedException {
        Ignite ignite = IgniteSpring.start(igniteCfg, applicationContext);
        ignite.active(true);
        ignite.getOrCreateCache(cacheConfig);
        publisher.publishEvent(new CacheReadyEvent(EventConstants.KIPOD_EVENTS));
        return ignite;
    }
    
}