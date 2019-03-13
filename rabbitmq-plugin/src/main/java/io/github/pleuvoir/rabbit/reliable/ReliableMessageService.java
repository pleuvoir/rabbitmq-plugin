package io.github.pleuvoir.rabbit.reliable;

import io.github.pleuvoir.rabbit.reliable.jdbc.MessageCommitLog;

public interface ReliableMessageService {

	void insert(MessageCommitLog log);

	MessageCommitLog findById(String messageId);
	
	Integer updateById(MessageCommitLog log);

	void remove(String messageId);
}
