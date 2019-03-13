package io.github.pleuvoir.springboot.example.rabbit;

public class RabbitConstants {

	public static class Normal {

		public static final String EXCHANGE = "x.normal";

		public static final String QUEUE = "q.normal";

		public static final String ROUTING_KEY = "r.normal";
	}
	
	public static class Unack {

		public static final String EXCHANGE = "x.unack";

		public static final String QUEUE = "q.unack";

		public static final String ROUTING_KEY = "r.unack";
	}
	
	public static class Exception {

		public static final String EXCHANGE = "x.exception";

		public static final String QUEUE = "q.exception";

		public static final String ROUTING_KEY = "r.exception";
	}

}
