package io.github.pleuvoir.rabbit.reliable;

public interface ExcuteWithTransaction {

	void actualExcute(RabbitConsumeCallBack callBack, String messageId) throws Exception;

}
