package io.github.pleuvoir.rabbit.reliable.jdbc;

import io.github.pleuvoir.rabbit.RabbitConsumeException;
import io.github.pleuvoir.rabbit.RetryStrategy;
import io.github.pleuvoir.rabbit.reliable.MessageCommitLog;
import io.github.pleuvoir.rabbit.reliable.MessageLogReposity;
import io.github.pleuvoir.rabbit.reliable.RabbitConsumeCallBack;
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

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class JDBCExcuteWithTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCExcuteWithTransaction.class);

	@Resource(name = "jdbcMessageReposity")
	private MessageLogReposity messageReposity;
	@Autowired
	private DataSourceTransactionManager txManager;

	public void actualExcute(RabbitConsumeCallBack callBack, String messageId) throws RabbitConsumeException {

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

        final Integer maxRetry = prevMessageLog.getMaxRetry();
        final Integer retryCount = prevMessageLog.getRetryCount();
        final String body = prevMessageLog.getBody();

        if (retryCount > 0) {
            LOGGER.info("*[messageId={}] 消息第{}次重试，消息内容{}。", messageId, retryCount, body);
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
			LOGGER.warn("*[messageId={}] 业务执行失败，已回滚。", messageId, e);
            RetryStrategy retryStrategy;
            if (maxRetry == 0 || retryCount == maxRetry) {
                retryStrategy = RetryStrategy.DISABLE;
                changeToFail(messageId);
                LOGGER.warn("*[messageId={}] 业务执行失败，不进行重试，已更新消息日志为消费失败，retryCount={}，maxRetry={}。", messageId, retryCount, maxRetry);
            } else {
                retryStrategy = RetryStrategy.ENABLE;
                messageReposity.incrementRetryCount(messageId); // 如果在这里宕机会多重试一次可以接受
                LOGGER.warn("*[messageId={}] 业务执行失败，即将进行第{}次重试。", messageId, retryCount + 1);
            }
            throw new RabbitConsumeException(retryStrategy, e);
		}
	}

	private void changeToSuccess(String messageId) {
		messageReposity.updateMessageLogStatus(messageId, MessageCommitLog.CONSUMER_SUCCESS, LocalDateTime.now());
	}

	private void changeToFail(String messageId) {
		messageReposity.updateMessageLogStatus(messageId, MessageCommitLog.CONSUMER_FAIL, LocalDateTime.now());
	}

}
