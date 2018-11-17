package io.github.pleuvoir.rabbit.support.producer;

public interface MQMessageProducer<M extends MQMessage> {

	void send(M message);
}
