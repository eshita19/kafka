package com.esh.kafka.twitter;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterProducer {
	private final String CONSUMER_KEY = "6NuBkMxXcKhbLJ1YZWjcGxdkw";
	private final String CONSUMER_SECRET = "ToQUxJBwhHgt0ikS1gJkwix8QcLv0WZQ86aKmyxs0Q6XFaszwn";
	private final String TOKEN = "1262345478156333057-mUOhT5Jnw2AyyL7DmxH4wItdw2iOcR";
	private final String SECRET = "yIcE0spMd7hIR4ekRxhp4PG18hb5FUvHGqG721U1lMlF4";
	private final String TOPIC_NAME = "twitter_tweets";
	private final Logger logger = LoggerFactory.getLogger(TwitterProducer.class);

	public TwitterProducer() {
	}

	public static void main(String[] args) {
		new TwitterProducer().run();
	}

	public void run() {
		// Set up your blocking queues: Be sure to size these properly based on expected
		// TPS of your stream
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);

		// Create a twitter client
		Client client = createTwitterClient(msgQueue);
		// Establish connection
		client.connect();

		try (// Create a kafka producer
				KafkaProducer<String, String> producer = createKafkaProducer()) {

			// loop to send tweets to kafka
			// on a different thread, or multiple different threads....
			while (!client.isDone()) {
				String msg = null;
				try {
					msg = msgQueue.poll(5, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
					client.stop();
				}
				if (msg != null) {
					// Messages with a key will always go to same partition
					ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_NAME, msg);
					producer.send(record, new Callback() {
						@Override
						public void onCompletion(RecordMetadata metadata, Exception e) {
							// Message sent successfully
							if (e != null) {
								logger.error("Failed to send message to broker " + metadata.topic() + "Partition: "
										+ metadata.partition());
							}
						}
					});// Async operation
					logger.info(msg);
				}
			}
		}
	}

	private KafkaProducer<String, String> createKafkaProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // kafka port
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//Safe producer properties: messaged are not lost
		props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
		props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // kafka > 1.1
		
		
		//High throughput producer with some latency
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024)); //32kb
		props.put(ProducerConfig.LINGER_MS_CONFIG, "20");
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
		
		return new KafkaProducer<String, String>(props);
	}

	public Client createTwitterClient(BlockingQueue<String> msgQueue) {

		// Declare the host you want to connect to, the endpoint, and authentication
		// (basic auth or oauth)
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some track terms
		List<String> terms = Lists.newArrayList("bitcoin");
		hosebirdEndpoint.trackTerms(terms);
		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, SECRET);
		
		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") // optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));

		return builder.build();
	}

}
