package io.github.pleuvoir.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.pleuvoir.test.model.NormalMessage;
import io.github.pleuvoir.test.producer.NormalMessageProducer;

public class NormalMessageExampleTests  extends BaseTest {

	@Autowired
	private NormalMessageProducer mormalMessageProducer;
	
	
	int num = 50;
	
	CountDownLatch countDownLatch = new CountDownLatch(num);

	@Test
	public void contextLoads() throws InterruptedException { 
		
		NormalMessage msg = new NormalMessage();
		msg.setId("1");
		
		for (int i = 0; i < num; i++) {
			new Thread(new ProducerThead(msg)).start();
			countDownLatch.countDown();
		}
		
		TimeUnit.SECONDS.sleep(60);
	}
	
	
	public class ProducerThead implements Runnable {

		private NormalMessage msg;

		public ProducerThead(NormalMessage msg) {
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
			mormalMessageProducer.send(msg);
		}
	}

}
