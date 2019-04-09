package io.github.pleuvoir.rabbit.reliable.jdbc;

import java.time.LocalDateTime;

public class MessageCommitLog {

	public static final String PREPARE_TO_BROKER = "0";

	public static final String CONSUMER_SUCCESS = "1";

	public static final String CONSUMER_FAIL = "2";

	private String id; // 消息编号

	private LocalDateTime createTime; // 创建时间

	private LocalDateTime updateTime; // 更新时间

	private String status; // 消息投递状态

	private Integer version; // 版本号

	private Integer maxRetry; // 最大重试次数

	public MessageCommitLog() {
		super();
	}


	public MessageCommitLog(String id, LocalDateTime createTime, LocalDateTime updateTime, String status) {
		this.id = id;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.status = status;
	}

	public static MessageCommitLog buildPrepareMessage(String messageId) {
		return new MessageCommitLog(messageId, LocalDateTime.now(), null, MessageCommitLog.PREPARE_TO_BROKER);
	}

	// getter and setter

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(Integer maxRetry) {
		this.maxRetry = maxRetry;
	}


}
