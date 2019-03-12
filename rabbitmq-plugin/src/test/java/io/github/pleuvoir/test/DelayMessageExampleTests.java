package io.github.pleuvoir.test;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.pleuvoir.test.model.DelayMessage;
import io.github.pleuvoir.test.producer.DelayMessageProducer;

public class DelayMessageExampleTests  extends BaseTest {

	@Autowired
	private DelayMessageProducer delayMessageProducer;
	
	
	int num = 1;
	
	CountDownLatch countDownLatch = new CountDownLatch(num);

	@Test
	public void contextLoads() throws InterruptedException { 
		
		DelayMessage msg = new DelayMessage();
		msg.setId("1");
		msg.setBeginTime(LocalDateTime.now().plusSeconds(50));
		
		for (int i = 0; i < num; i++) {
			new Thread(new ProducerThead(msg)).start();
			countDownLatch.countDown();
		}
		
		TimeUnit.SECONDS.sleep(60);
	}
	
	
	public class ProducerThead implements Runnable {

		private DelayMessage msg;

		public ProducerThead(DelayMessage msg) {
			super();
			this.msg = msg;
		}

		@Override
		public void run() {
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			delayMessageProducer.send(msg);
		}
	}

}
