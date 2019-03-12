package io.github.pleuvoir.rabbit.support.producer;

import com.alibaba.fastjson.JSON;

public abstract class MQMessage {

	public String toJSON() {
		return JSON.toJSONString(this);
	}
}
