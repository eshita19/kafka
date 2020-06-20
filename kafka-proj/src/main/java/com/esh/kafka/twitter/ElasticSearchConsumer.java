package com.esh.kafka.twitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ElasticSearchConsumer {

	public static void main(String[] args) throws IOException {

		// Get reference of rest client
		RestHighLevelClient client = getRestHighLevelClient();
		// Get Kafka consumer subscibed to topic twitter_tweets
		KafkaConsumer<String, String> consumer = getKafkaConsumer("twitter_tweets");
		// poll for new data and put in elastic search
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				// Add id to IndexRequest to make Consumer idempotent. Since by default "At
				// least once" strategy is used.
				IndexRequest indexRequest = new IndexRequest("twitter", "tweets", record.topic() + record.offset())
						.source(record.value(), XContentType.JSON);
				IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
				System.out.println(indexResponse.getId());
				System.out.println("Consumer Data: " + "Index: " + indexResponse.getId() + " Topic: " + record.topic()
						+ ", Key: " + record.key() + ", Value: " + record.value());
			}
			//Manual commit offset after processing is done
			consumer.commitSync();
		}
	}

	public static KafkaConsumer<String, String> getKafkaConsumer(String topic) {
		// Consumer proper
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); //Disable auto commit for at least one semantic

		// Create consumer
		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties)) {
			// Subscribe consumer to topic
			consumer.subscribe(Collections.singleton("topic"));
			return consumer;
		}
	}

	public static RestHighLevelClient getRestHighLevelClient() {
		String hostName = "kafka-testing-3739041159.ap-southeast-2.bonsaisearch.net";
		String userName = "gt09hmyb61";
		String password = "1b14x16ozv";
		final CredentialsProvider credentialProvider = new BasicCredentialsProvider();
		credentialProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(hostName, 443, "https"))
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialProvider);
					}
				});

		return new RestHighLevelClient(restClientBuilder);
	}
}
