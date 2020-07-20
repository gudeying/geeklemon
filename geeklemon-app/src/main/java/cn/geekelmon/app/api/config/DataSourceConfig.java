package cn.geekelmon.app.api.config;

import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.Value;
import cn.hutool.db.ds.simple.SimpleDataSource;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.sql.DriverManager;


@Bean(single = true)
public class DataSourceConfig {
    @Value(name = "lemon.data.url")
    private String url;
    @Value(name = "lemon.data.user")
    private String user;
    @Value(name = "lemon.data.password")
    private String password;

    @Bean(single = true)
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(user);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(50);
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        return druidDataSource;
    }
}
