package com.atguigu.gulimall.ware.config;

import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MySeataConfig
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/27 11:49
 * @Version 1.0
 */

@Configuration
public class MySeataConfig {


//    @Autowired
//    DataSourceProperties dataSourceProperties;
//
//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties){
//        HikariDataSource build = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if(StringUtils.hasText(dataSourceProperties.getName())){
//            build.setPoolName(dataSourceProperties.getName());
//        }
//        return new DataSourceProxy(build);
//    }
}
