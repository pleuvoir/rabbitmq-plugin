package io.github.pleuvoir.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.github.pleuvoir.rabbit.RabbitMQConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RabbitMQConfiguration.class})
public abstract class BaseTest {

}
