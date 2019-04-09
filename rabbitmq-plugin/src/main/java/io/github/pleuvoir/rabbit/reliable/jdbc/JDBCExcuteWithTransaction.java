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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import io.github.pleuvoir.rabbit.reliable.ExcuteWithTransaction;
import io.github.pleuvoir.rabbit.reliable.RabbitConsumeCallBack;
import io.github.pleuvoir.rabbit.reliable.ReliableMessageService;

@Service
public class JDBCExcuteWithTransaction implements ExcuteWithTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCExcuteWithTransaction.class);
	
	@Autowired ReliableMessageService reliableMessageService;
	
	@Autowired
	private DataSourceTransactionManager txManager;
	
	//@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
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
			LOGGER.warn("*[messageId={}] 消息已经消费成功，可能是应答时出现故障，此次消息被忽略。", messageId);
			return;
		}

		if (prevMessageLog.getVersion() >= prevMessageLog.getMaxRetry()) {
			LOGGER.warn("*[messageId={}] 消息重试次数{}已超过最大重试次数{}，消息最终丢弃。", messageId, prevMessageLog.getVersion(),
					prevMessageLog.getMaxRetry());
			return;
		}
		
		
		// 开启事物
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = txManager.getTransaction(def);
		// 执行业务
		try {
			callBack.doInTransaction();
			txManager.commit(txStatus);
		} catch (Exception e) {
			txManager.rollback(txStatus);
			prevMessageLog.setUpdateTime(LocalDateTime.now());
			prevMessageLog.setStatus(MessageCommitLog.CONSUMER_FAIL);
			reliableMessageService.updateById(prevMessageLog);
			LOGGER.info("*[messageId={}] 业务异常，已更新消息日志为消费失败。", messageId);
			throw e;
		}

		prevMessageLog.setUpdateTime(LocalDateTime.now());
		prevMessageLog.setStatus(MessageCommitLog.CONSUMER_SUCCESS);
		reliableMessageService.updateById(prevMessageLog);
		
		LOGGER.info("*[messageId={}] 已更新消息日志为成功。", messageId);
	}

}
