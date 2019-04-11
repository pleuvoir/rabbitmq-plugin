package test.mq;

import io.github.pleuvoir.springboot.example.rabbit.MessagePayload;
import io.github.pleuvoir.springboot.example.rabbit.producer.ExceptionProducer;
import io.github.pleuvoir.springboot.example.rabbit.producer.NormalMessageProducer;
import io.github.pleuvoir.springboot.example.rabbit.producer.UnackMessageProducer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.BaseTest;

import java.io.IOException;

public class MainTest extends BaseTest {
	
	@Autowired
	private NormalMessageProducer normalMessageProducer;
	@Autowired
	private ExceptionProducer exceptionProducer;
	@Autowired
	private UnackMessageProducer unackMessageProducer;


	@Test
	public void testNormal() throws IOException {

		MessagePayload messagePayload = new MessagePayload();
		messagePayload.setPayload(String.valueOf(System.identityHashCode(this)));

		normalMessageProducer.send(messagePayload);
		
	}
	
	@Test
	public void testException() throws IOException, InterruptedException {

		MessagePayload messagePayload = new MessagePayload();
		messagePayload.setPayload(String.valueOf(System.identityHashCode(this)));

		exceptionProducer.send(messagePayload);
		
		//System.in.read();
		Thread.currentThread().join();
	}
	

	
	@Test
	public void testUnack() throws IOException {

		MessagePayload messagePayload = new MessagePayload();
		messagePayload.setPayload(String.valueOf(System.identityHashCode(this)));

		unackMessageProducer.send(messagePayload);
		
	}

}
