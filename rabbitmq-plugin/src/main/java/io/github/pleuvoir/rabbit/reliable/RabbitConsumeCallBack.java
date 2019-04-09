package io.github.pleuvoir.rabbit.reliable;

@FunctionalInterface
public interface RabbitConsumeCallBack {

	void doInTransaction() throws Exception;

}