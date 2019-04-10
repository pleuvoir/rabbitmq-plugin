package io.github.pleuvoir.rabbit.reliable.jdbc;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import io.github.pleuvoir.rabbit.reliable.MessageCommitLog;
import io.github.pleuvoir.rabbit.reliable.MessageLogReposity;
import io.github.pleuvoir.rabbit.reliable.RabbitConsumeCallBack;

@Service
public class JDBCExcuteWithTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCExcuteWithTransaction.class);

	@Autowired
	private MessageLogReposity messageReposity;
	@Autowired
	private DataSourceTransactionManager txManager;

	public void actualExcute(RabbitConsumeCallBack callBack, String messageId) throws Exception {

		if (StringUtils.isBlank(messageId)) {
			LOGGER.warn("*messageId 为空，忽略此次消息消费。");
			return;
		}

		Assert.notNull(callBack, "业务回调不能为空");

		MessageCommitLog prevMessageLog = messageReposity.findById(messageId);
		if (prevMessageLog == null) {
			LOGGER.warn("*[messageId={}] 未能获取消息日志，忽略此次消息消费。", messageId);
			return;
		}

		if (prevMessageLog.getStatus().equals(MessageCommitLog.CONSUMER_SUCCESS)) {
			LOGGER.warn("*[messageId={}] 消息已经消费成功，可能是应答时出现故障，此次消息被忽略。", messageId);
			return;
		}

		if (prevMessageLog.getRetryCount() >= prevMessageLog.getMaxRetry()) {
			changeToFail(messageId);
			LOGGER.warn("*[messageId={}] 消息第{}次重试，消息内容{}，重试已超过最大重试次数{}，此次消息不进行处理，已更新消息日志为消费失败。", messageId,
					prevMessageLog.getRetryCount(), prevMessageLog.getBody(), prevMessageLog.getMaxRetry());
			return;
		}

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = txManager.getTransaction(def);
		try {
			// 执行业务
			callBack.doInTransaction();
			changeToSuccess(messageId);
			txManager.commit(txStatus);
			LOGGER.info("*[messageId={}] 已更新消息日志为成功。", messageId);
		} catch (Throwable e) {
			txManager.rollback(txStatus);
			messageReposity.incrementRetryCount(messageId); // 如果在这里宕机会多重试一次可以接受
			throw e;
		}
	}

	private void changeToSuccess(String messageId) {
		messageReposity.updateMessageLogStatus(messageId, MessageCommitLog.CONSUMER_SUCCESS, LocalDateTime.now());
	}

	private void changeToFail(String messageId) {
		messageReposity.updateMessageLogStatus(messageId, MessageCommitLog.CONSUMER_FAIL, LocalDateTime.now());
	}

}
