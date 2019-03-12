package io.github.pleuvoir.test;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.pleuvoir.test.model.FixedTimeMessage;
import io.github.pleuvoir.test.producer.FixedTimeMessageProducer;

public class FixTimeMessageExampleTests  extends BaseTest {

	@Autowired
	private FixedTimeMessageProducer fixedTimeMessageProducer;
	
	
	int num = 10;
	
	CountDownLatch countDownLatch = new CountDownLatch(num);

	@Test
	public void contextLoads() throws InterruptedException { 
		
		for (int i = 0; i < num; i++) {
			FixedTimeMessage msg = new FixedTimeMessage();
			msg.setId(String.valueOf(i));
			msg.setExcutetime(LocalDateTime.now().plusSeconds(ThreadLocalRandom.current().nextLong(60)));
			new Thread(new ProducerThead(msg)).start();
			countDownLatch.countDown();
		}
		
		TimeUnit.SECONDS.sleep(60);
	}
	
	
	public class ProducerThead implements Runnable {

		private FixedTimeMessage msg;

		public ProducerThead(FixedTimeMessage msg) {
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
			fixedTimeMessageProducer.send(msg);
		}
	}

}
