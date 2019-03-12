package io.github.pleuvoir.rabbit.reliable.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import io.github.pleuvoir.rabbit.reliable.ReliableMessageService;
import io.github.pleuvoir.rabbit.utils.ClassHelper;
import io.github.pleuvoir.rabbit.utils.DateFormat;

public class JDBCReliableMessageService implements ReliableMessageService, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCReliableMessageService.class);

	@Resource(name = "pluginJdbcTemplate")
	private JdbcTemplate tpl;

	@Override
	public void insert(RabbitMessageLog log) {
		String messageId = log.getId();
		String status = log.getStatus();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("保存MQ消息记录，messageId：{}，status：{}", messageId, status);
		}
		tpl.update("insert into rabbitmq_message_log (ID, STATUS, CREATE_TIME) values (?, ?, ?)",
				new Object[]{messageId, status, log.getCreateTime()});
	}

	@Override
	public RabbitMessageLog findById(String messageId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("查询MQ消息记录，messageId：{}", messageId);
		}

		List<RabbitMessageLog> result = tpl.query(

				"select id,status,create_time,update_time from rabbitmq_message_log where id = ?",

				new Object[]{messageId}, new RowMapper<RabbitMessageLog>() {

					@Override
					public RabbitMessageLog mapRow(ResultSet rs, int rowNum) throws SQLException {
						RabbitMessageLog log = new RabbitMessageLog();
						log.setId(rs.getString("id"));
						log.setStatus(rs.getString("status"));
						String createTime = rs.getString("create_time");
						if (StringUtils.isNotBlank(createTime)) {
							log.setCreateTime(DateFormat.DATETIME_MILLISECOND_1.parse(createTime));
						}
						String updateTime = rs.getString("update_time");
						if (StringUtils.isNotBlank(updateTime)) {
							log.setCreateTime(DateFormat.DATETIME_MILLISECOND_1.parse(updateTime));
						}
						return log;
					}
				});

		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	@Override
	public void remove(String messageId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("删除MQ消息记录，messageId：{}", messageId);
		}
		tpl.update("delete from rabbitmq_message_log where id = ?", new Object[]{messageId});
	}

	@Override
	public Integer updateById(RabbitMessageLog log) {
		String messageId = log.getId();
		String status = log.getStatus();
		LocalDateTime updateTime = log.getUpdateTime();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("更新MQ消息记录，messageId：{}，status：{}", messageId, status);
		}

		int update = tpl.update("update rabbitmq_message_log set status = ? , update_time = ? where id = ?",
				new Object[]{status, updateTime, messageId});

		if (update == 0) {
			throw new RuntimeException(String.format("更新MQ消息记录失败，messageId：%s，status：%s", messageId, status));
		}
		return null;
	}

	// 创建表操作无需回滚
	@Override
	public void afterPropertiesSet() throws Exception {
		Integer count = tpl.queryForObject("SELECT COUNT(*) FROM User_Tables WHERE table_name = 'RABBITMQ_MESSAGE_LOG'",
				Integer.TYPE);
		if (count == 0) {
			this.createDBTable();
		}
	}

	private void createDBTable() {
		String fileName = "oracle_create_table.sql";
		try {
			 Enumeration<java.net.URL> urls;
			   ClassLoader classLoader =  ClassHelper.getClassLoader(JDBCReliableMessageService.class);
	            if (classLoader != null) {
	                urls = classLoader.getResources(fileName);
	            } else {
	                urls = ClassLoader.getSystemResources(fileName);
	            }
			if (urls != null) {
				while (urls.hasMoreElements()) {
					java.net.URL url = urls.nextElement();
					
					String sql = file2String(url);
					LOGGER.info("创建可靠消息日志表，\n SQL: {}", sql);
					tpl.execute(sql);
				}
			}
		
		} catch (Exception e) {
			LOGGER.error("创建可靠消息记录表失败", e);
			throw new IllegalStateException("创建可靠消息记录表失败", e);
		}
	}

	public static String file2String(final URL url) {
		InputStream in = null;
		try {
			URLConnection urlConnection = url.openConnection();
			urlConnection.setUseCaches(false);
			in = urlConnection.getInputStream();
			int len = in.available();
			byte[] data = new byte[len];
			in.read(data, 0, len);
			return new String(data, "UTF-8");
		} catch (Exception ignored) {
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
		return null;
	}

}
