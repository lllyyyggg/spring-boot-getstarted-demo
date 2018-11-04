package com.lanyage.springbootgetstarted.ehcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

//@Configuration
//@EnableCaching
public class EhCacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(EhCacheConfig.class);
    @Bean(name = "ehcacheManager")
    public EhCacheCacheManager ehcacheManager(@Qualifier("ehcacheManagerFactoryBean") EhCacheManagerFactoryBean bean) {
        logger.info("Creating [{}]'s instance.", "EhCacheCacheManager");
        return new EhCacheCacheManager(bean.getObject());
    }

    @Bean(name = "ehcacheManagerFactoryBean")
    public EhCacheManagerFactoryBean ehcacheManagerFactoryBean() {
        logger.info("Creating [{}]'s instance.", "EhCacheManagerFactoryBean");
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        ehCacheManagerFactoryBean.setShared(true);
        return ehCacheManagerFactoryBean;
    }
}
