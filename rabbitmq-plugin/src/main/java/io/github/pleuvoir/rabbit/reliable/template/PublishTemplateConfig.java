package io.github.pleuvoir.rabbit.reliable.template;

public class PublishTemplateConfig {

	private Integer maxRetry; // 最大重试次数

	public Integer getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(Integer maxRetry) {
		this.maxRetry = maxRetry;
	}
}
