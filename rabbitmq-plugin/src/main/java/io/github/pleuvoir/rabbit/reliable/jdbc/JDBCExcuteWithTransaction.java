package io.github.pleuvoir.rabbit.reliable.jdbc;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.github.pleuvoir.rabbit.reliable.ExcuteWithTransaction;
import io.github.pleuvoir.rabbit.reliable.ReliableMessageService;

@Service
public class JDBCExcuteWithTransaction implements ExcuteWithTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCExcuteWithTransaction.class);
	
	@Autowired ReliableMessageService reliableMessageService;
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public void actualExcute(RabbitConsumeCallBack callBack, String messageId) throws Exception {
		
		if (StringUtils.isBlank(messageId)) {
			LOGGER.warn("*messageId 为空，忽略此次消息消费。");
			return;
		}
		
		Assert.notNull(callBack, "业务回调不能为空");
		
		MessageCommitLog prevMessageLog = reliableMessageService.findById(messageId);
		if (prevMessageLog == null) {
			LOGGER.warn("*[messageId={}] 未能获取消息日志，忽略此次消息消费。", messageId);
			return;
		}

		if (prevMessageLog.getStatus().equals(MessageCommitLog.CONSUMER_SUCCESS)) {
			LOGGER.warn("*[messageId={}] 消息日志表明，此消息已经消费成功，可能是应答时出现故障，此次消息被忽略。", messageId);
			return;
		}

		// 执行业务
		callBack.doInTransaction();

		prevMessageLog.setUpdateTime(LocalDateTime.now());
		prevMessageLog.setStatus(MessageCommitLog.CONSUMER_SUCCESS);
		reliableMessageService.updateById(prevMessageLog);
		
		LOGGER.info("*[messageId={}] 已更新消息日志为成功。", messageId);
	}

	@FunctionalInterface
	public interface RabbitConsumeCallBack {
		void doInTransaction() throws Exception;
	}
	
}
