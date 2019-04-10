package io.github.pleuvoir.rabbit.reliable.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishTemplateConfig {

	private Integer maxRetry; // 最大重试次数

}
