package io.github.pleuvoir.rabbit.reliable.jdbc;

import java.time.LocalDateTime;

public class RabbitMessageLog {
	
	public static final String PREPARE_TO_BROKER = "0";
	
    public static final String CONSUMER_SUCCESS = "1";

	private String id; // 消息编号

	private LocalDateTime createTime; // 创建时间

	private LocalDateTime updateTime; // 更新时间

	private String status; // 消息投递状态


	public RabbitMessageLog() {
		super();
	}

	public RabbitMessageLog(String id, LocalDateTime createTime, LocalDateTime updateTime, String status) {
		this.id = id;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.status = status;
	}

	public static RabbitMessageLog buildPrepareMessage(String messageId) {
		return new RabbitMessageLog(messageId, LocalDateTime.now(), null, RabbitMessageLog.PREPARE_TO_BROKER);
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
