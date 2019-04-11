package io.github.pleuvoir.test.jdbc;

import io.github.pleuvoir.rabbit.reliable.MessageCommitLog;
import io.github.pleuvoir.rabbit.reliable.jdbc.JDBCMessageLogRepository;
import io.github.pleuvoir.rabbit.utils.Generator;
import io.github.pleuvoir.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class ReliableMessageServiceTest extends BaseTest {

	@Autowired
    JDBCMessageLogRepository reliableMessageService;


	@Test
	@Transactional 
	public void initDBTest() {

		String messageId = Generator.nextUUID();
		MessageCommitLog log = new MessageCommitLog();
		log.setId(messageId);
		log.setStatus("0");
		log.setCreateTime(LocalDateTime.now());
		reliableMessageService.insert(log);

		MessageCommitLog preLog;
		preLog = reliableMessageService.findById(messageId);
		Assert.assertNotNull(preLog);
		Assert.assertEquals(messageId, preLog.getId());
		Assert.assertEquals("0", preLog.getStatus());
		Assert.assertNotNull(preLog.getCreateTime());
		Assert.assertNull(preLog.getUpdateTime());


		MessageCommitLog afterLog = reliableMessageService.findById(messageId);

		Assert.assertNotNull(afterLog);
		Assert.assertEquals(afterLog.getId(), preLog.getId());
		Assert.assertEquals("1", afterLog.getStatus());
		Assert.assertNotNull(afterLog.getCreateTime());
		Assert.assertNull(afterLog.getUpdateTime());
		
		reliableMessageService.remove(messageId);
		
		Assert.assertNull(reliableMessageService.findById(messageId));
	}


}
