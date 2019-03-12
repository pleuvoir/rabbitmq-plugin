package io.github.pleuvoir.rabbit.reliable.jdbc;

/**
 * 插件数据源配置
 *
 */
public class PluginDataSourceConfig {

	private String jdbcDriverClass;

	private String jdbcURL;

	private String jdbcUser;

	private String jdbcPassword;

	private int initialSize;

	private int maxActive;

	private int minIdle;

	private long maxWait;

	private long timeBetweenEvictionRunsMillis;

	private long minEvictableIdleTimeMillis;

	private String validationQuery;

	public static PluginDataSourceConfig defaultConfig() {
		PluginDataSourceConfig pluginDataSourceConfig = new PluginDataSourceConfig();
		pluginDataSourceConfig.setJdbcDriverClass("oracle.jdbc.OracleDriver");
		pluginDataSourceConfig.setJdbcURL("jdbc:oracle:thin:@192.168.100.8:1521:cacplat");
		pluginDataSourceConfig.setJdbcUser("auction_test");
		pluginDataSourceConfig.setJdbcPassword("auction_test");
		pluginDataSourceConfig.setInitialSize(1);
		pluginDataSourceConfig.setMaxActive(3);
		pluginDataSourceConfig.setMinIdle(1);
		pluginDataSourceConfig.setMaxWait(60000);
		pluginDataSourceConfig.setTimeBetweenEvictionRunsMillis(60000);
		pluginDataSourceConfig.setMinEvictableIdleTimeMillis(25200000);
		pluginDataSourceConfig.setValidationQuery("select 1 from dual");
		return pluginDataSourceConfig;
	}

	public String getJdbcDriverClass() {
		return jdbcDriverClass;
	}

	public void setJdbcDriverClass(String jdbcDriverClass) {
		this.jdbcDriverClass = jdbcDriverClass;
	}

	public String getJdbcURL() {
		return jdbcURL;
	}

	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	public String getJdbcUser() {
		return jdbcUser;
	}

	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

}
