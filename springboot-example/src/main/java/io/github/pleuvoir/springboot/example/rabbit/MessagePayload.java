package io.github.pleuvoir.springboot.example.rabbit;

import io.github.pleuvoir.rabbit.MQMessage;

public class MessagePayload extends MQMessage{

	private String payload;

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

}
