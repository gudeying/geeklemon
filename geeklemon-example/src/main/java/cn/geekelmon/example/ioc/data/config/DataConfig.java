package cn.geekelmon.example.ioc.data.config;

import cn.geeklemon.core.context.annotation.Bean;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2020/1/10 11:04
 * Modified by : kavingu
 */
@Bean
public class DataConfig {
    @Bean
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/website?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");
        druidDataSource.setUsername("geeklemon");
        druidDataSource.setPassword("geeklemon2019!");
        return druidDataSource;
    }
}
