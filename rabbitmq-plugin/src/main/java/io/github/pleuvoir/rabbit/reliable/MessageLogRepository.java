package io.github.pleuvoir.rabbit.reliable;

import java.time.LocalDateTime;

public interface MessageLogRepository {

	/**
	 * 插入一条消息记录
	 */
	void insert(MessageCommitLog log);

	/**
	 * 查找消息记录
	 */
	MessageCommitLog findById(String messageId);

	/**
	 * 增加一次重试次数
	 */
	void incrementRetryCount(String messageId);

	/**
	 * 移除消息记录
	 */
	void remove(String messageId);

	/**
	 * 更新消息最终状态
	 * @param messageId 消息标识
	 * @param status	消息状态
	 * @param updateTime 更新时间
	 */
	void updateMessageLogStatus(String messageId, String status, LocalDateTime updateTime);
}
