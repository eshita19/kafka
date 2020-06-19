
package com.emathur.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {
	private static final String TOPIC_NAME = "first_topic";
	private static final Logger logger = LoggerFactory.getLogger(Producer.class);

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// Config properties of Producer
		Properties configs = new Properties();
		configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // kafka port
		configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		try (KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs)) {
			for (int i = 0; i < 10; i++) {
				// Messages with a key will always go to same partition
				ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_NAME, "key" + i,
						"This is message " + i);
				producer.send(record, new Callback() {
					@Override
					public void onCompletion(RecordMetadata metadata, Exception e) {
						// Message sent successfully
						if (null == e) {
							logger.info("Message successfully sent: Topic: " + metadata.topic() + "Partition: "
									+ metadata.partition());
						}
					}
				});// Async operation
			}
		}
	}
}
