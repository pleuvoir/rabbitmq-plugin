package io.github.pleuvoir.rabbit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;

import io.github.pleuvoir.rabbit.reliable.jdbc.PluginDataSourceConfig;

@Configuration
@EnableTransactionManagement
@ComponentScan({"io.github.pleuvoir.rabbit.reliable"})
public class DataSourceConfiguration {

	private DataSource dataSource;

	private PluginDataSourceConfig dataSourceConfig;

	@Autowired(required = false)
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean(name = "pluginDataSourceConfig")
	public PluginDataSourceConfig getDataSourceConfig() {
		if (dataSourceConfig == null) {
			return PluginDataSourceConfig.defaultConfig();
		}
		return dataSourceConfig;
	}

	@Bean(name = "pluginDataSource")
	public DataSource getDataSource(@Qualifier("pluginDataSourceConfig") PluginDataSourceConfig dataSourceConfig) {
		if (dataSource != null) {
			return dataSource;
		}
		DruidDataSource ds = new DruidDataSource();
		ds.setDriverClassName(dataSourceConfig.getJdbcDriverClass());
		ds.setUrl(dataSourceConfig.getJdbcURL());
		ds.setUsername(dataSourceConfig.getJdbcUser());
		ds.setPassword(dataSourceConfig.getJdbcPassword());
		ds.setInitialSize(dataSourceConfig.getInitialSize());
		ds.setMaxActive(dataSourceConfig.getMaxActive());
		ds.setMinIdle(dataSourceConfig.getMinIdle());
		ds.setMaxWait(dataSourceConfig.getMaxWait());
		ds.setTimeBetweenEvictionRunsMillis(dataSourceConfig.getTimeBetweenEvictionRunsMillis());
		ds.setMinEvictableIdleTimeMillis(dataSourceConfig.getMinEvictableIdleTimeMillis());
		ds.setValidationQuery(dataSourceConfig.getValidationQuery());
		ds.setTestOnBorrow(false);
		ds.setTestOnReturn(false);
		ds.setTestWhileIdle(true);
		return ds;
	}

	@Bean(name = "pluginJdbcTemplate")
	public JdbcTemplate getJdbcTemplate(@Qualifier("pluginDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean("transactionManager")
	public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
