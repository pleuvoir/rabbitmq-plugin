package io.github.pleuvoir.rabbit.reliable;

import io.github.pleuvoir.rabbit.reliable.jdbc.RabbitMessageLog;

public interface ReliableMessageService {

	void insert(RabbitMessageLog log);

	RabbitMessageLog findById(String messageId);
	
	Integer updateById(RabbitMessageLog log);

	void remove(String messageId);
}
