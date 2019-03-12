package test.mq;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.pleuvoir.springboot.example.rabbit.MessagePayload;
import io.github.pleuvoir.springboot.example.rabbit.NormalMessageProducer;
import test.BaseTest;

public class MainTest extends BaseTest {
	@Autowired
	private NormalMessageProducer producer;


	@Test
	public void test() throws IOException {

		MessagePayload messagePayload = new MessagePayload();
		messagePayload.setPayload(String.valueOf(System.identityHashCode(this)));

		producer.send(messagePayload);
		
		
		System.in.read();
	}

}
