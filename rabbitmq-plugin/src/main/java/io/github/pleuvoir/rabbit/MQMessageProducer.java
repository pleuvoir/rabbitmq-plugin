package io.github.pleuvoir.rabbit;

public interface MQMessageProducer<M extends MQMessage> {

	void send(M message);
}
