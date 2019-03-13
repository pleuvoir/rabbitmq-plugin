package io.github.pleuvoir.rabbit.reliable;

import io.github.pleuvoir.rabbit.reliable.jdbc.JDBCExcuteWithTransaction.RabbitConsumeCallBack;

public interface ExcuteWithTransaction {

	void actualExcute(RabbitConsumeCallBack callBack, String messageId) throws Exception;

}
