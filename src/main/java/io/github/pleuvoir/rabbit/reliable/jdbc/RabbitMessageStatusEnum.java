package io.github.pleuvoir.rabbit.reliable.jdbc;

public enum RabbitMessageStatusEnum {

	PREPARE_TO_BROKER(0), 
	CONSUMER_SUCCESS(1);

	private int status;

	private RabbitMessageStatusEnum(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
