package io.github.pleuvoir.rabbit.reliable.jdbc;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import io.github.pleuvoir.rabbit.reliable.MessageCommitLog;
import io.github.pleuvoir.rabbit.reliable.MessageLogReposity;

public class JDBCMessageLogReposity implements MessageLogReposity, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCMessageLogReposity.class);

	@Resource(name = "pluginJdbcTemplate")
	private JdbcTemplate tpl;

	@Override
	public void insert(MessageCommitLog log) {
		tpl.update(
				"insert into message_commit_log (ID, STATUS, MAX_RETRY, CREATE_TIME, RETRY_COUNT, BODY) values (?, ?, ?,?,?,?)",
				new Object[]{log.getId(), log.getStatus(), log.getMaxRetry(), log.getCreateTime(), log.getRetryCount(),
						log.getBody()});
	}

	@Override
	public void updateMessageLogStatus(String messageId, String status, LocalDateTime updateTime) {
		tpl.update("update message_commit_log set status = ? , update_time = ? where id = ?", new Object[]{status, updateTime, messageId});
	}

	@Override
	public void incrementRetryCount(String messageId) {
		tpl.update("update message_commit_log set retry_count = retry_count + 1 where id = ?", new Object[]{messageId});
	}
		
	@Override
	public MessageCommitLog findById(String messageId) {
		List<MessageCommitLog> result = tpl.query(
				"select id,status,create_time,update_time,max_retry,retry_count,body from message_commit_log where id = ?",
				new Object[]{messageId}, new RowMapper<MessageCommitLog>() {
					@Override
					public MessageCommitLog mapRow(ResultSet rs, int rowNum) throws SQLException {
						MessageCommitLog log = new MessageCommitLog();
						log.setId(rs.getString("id"));
						log.setStatus(rs.getString("status"));
						Timestamp createTime = rs.getTimestamp("create_time");
						if (createTime != null) {
							log.setCreateTime(createTime.toLocalDateTime());
						}
						Timestamp updateTime = rs.getTimestamp("update_time");
						if (updateTime != null) {
							log.setCreateTime(updateTime.toLocalDateTime());
						}
						log.setMaxRetry(rs.getInt("max_retry"));
						log.setBody(rs.getString("body"));
						log.setRetryCount(rs.getInt("retry_count"));
						return log;
					}
				});
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	@Override
	public void remove(String messageId) {
		tpl.update("delete from message_commit_log where id = ?", new Object[]{messageId});
	}


	// 创建表操作无需回滚
	@Override
	public void afterPropertiesSet() throws Exception {
		Integer count = tpl.queryForObject("SELECT COUNT(*) FROM User_Tables WHERE table_name = 'MESSAGE_COMMIT_LOG'",
				Integer.TYPE);
		if (count == 0) {
			this.createDBTable();
		}
	}

	private void createDBTable() {
		String fileName = "oracle_create_table.sql";
		try {
			String sql = IOUtils.resourceToString(fileName, 
					Charset.forName("UTF-8"),
					Thread.currentThread().getContextClassLoader());
			LOGGER.info("*创建可靠消息日志表，\n SQL: {}", sql);
			tpl.execute(sql);
		} catch (Exception e) {
			LOGGER.error("*创建可靠消息记录表失败", e);
			throw new IllegalStateException("创建可靠消息记录表失败", e);
		}
	}

}
