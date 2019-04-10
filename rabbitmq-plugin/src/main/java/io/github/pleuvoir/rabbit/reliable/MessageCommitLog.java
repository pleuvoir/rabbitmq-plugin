package io.github.pleuvoir.rabbit.reliable;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageCommitLog {

	public static final String PREPARE_TO_BROKER = "0";

	public static final String CONSUMER_SUCCESS = "1";

	public static final String CONSUMER_FAIL = "2";

	private String id; // 消息唯一标识

	private LocalDateTime createTime; // 创建时间

	private LocalDateTime updateTime; // 更新时间

	private String status; // 消息投递状态

	private Integer maxRetry; // 最大重试次数

	private Integer retryCount; // 重试次数

	private String body; // 消息内容

}
