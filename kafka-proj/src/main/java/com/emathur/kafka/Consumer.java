package com.emathur.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {
	private static Logger logger = LoggerFactory.getLogger(Consumer.class);

	public static void main(String[] args) {
		//Consumer proper
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
		
		//Create consumer
		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties)) {
			//Subscribe consumer to topic
			consumer.subscribe(Collections.singleton("first_topic"));
			//poll for new data
			while(true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for(ConsumerRecord<String, String> record: records) {
					logger.info("Consumer Data: Topic: " + record.topic() + ", Key: " + record.key() + ", Value: " + record.value());
				}
			}
		}
		
	}
}
