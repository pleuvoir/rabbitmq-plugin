package io.github.pleuvoir.rabbit.reliable.jdbc;

import java.time.LocalDateTime;

public class MessageCommitLog {
	
	public static final String PREPARE_TO_BROKER = "0";
	
    public static final String CONSUMER_SUCCESS = "1";

	private String id; // 消息编号

	private LocalDateTime createTime; // 创建时间

	private LocalDateTime updateTime; // 更新时间

	private String status; // 消息投递状态


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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

}
