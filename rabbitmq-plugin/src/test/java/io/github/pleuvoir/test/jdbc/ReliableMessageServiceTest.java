package io.github.pleuvoir.test.jdbc;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import io.github.pleuvoir.rabbit.reliable.ReliableMessageService;
import io.github.pleuvoir.rabbit.reliable.jdbc.MessageCommitLog;
import io.github.pleuvoir.rabbit.utils.Generator;
import io.github.pleuvoir.test.BaseTest;

public class ReliableMessageServiceTest extends BaseTest {

	@Autowired
	ReliableMessageService reliableMessageService;


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

		preLog.setStatus("1");
		preLog.setUpdateTime(LocalDateTime.now());
		reliableMessageService.updateById(preLog);

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
