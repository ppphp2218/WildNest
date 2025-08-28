package com.HuangYe.WildNest.config;

import com.alibaba.druid.pool.DruidDataSource;
// import com.alibaba.druid.support.http.StatViewServlet;
// import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.Map;

/**
 * 数据库配置类
 * 配置Druid数据源和监控
 */
@Configuration
public class DatabaseConfig {
    
    /**
     * 配置Druid数据源
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }
    
    // TODO: Druid监控配置暂时注释，等待Spring Boot 3完全兼容版本
    /*
    /**
     * 配置Druid监控页面
     */
    /*
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        
        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin");
        initParams.put("loginPassword", "admin123");
        initParams.put("allow", ""); // 默认允许所有访问
        
        bean.setInitParameters(initParams);
        return bean;
    }
    
    /**
     * 配置Druid Web监控过滤器
     */
    /*
    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());
        
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/druid/*");
        
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*"));
        
        return bean;
    }
    */
}