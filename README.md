# Kafka
- https://kafka.apache.org/documentation/

# Why Kafka?
 - It is a high throughput distributed mesaging system which provides decoupling of data streams and systems.
 - <img src="https://github.com/eshita19/kafka/blob/master/kafka1.png"></img>
 - Distributed, resilient architecture and fault tolerant.
 - Horizontal scalibility(https://searchcio.techtarget.com/definition/horizontal-scalability):
   - Can scale to 100s brokers
   - Can scale to millions of messages per second.
 - High performance - latency less than 10ms - real time.
 
# Use Cases:
 - Messaging system
 - Activity tracking.
 - Gather data from different locations.
 - Stream processing.
 - De-coupling of system dependencies.
 - Integration with many big data technologies.
 - Example:
   - Netflix uses to apply recommendations in real time while you are watching TV shows.
   - Uber uses Kafka to gather taxi and trip data in real time to compute the forecast demand and compute surge pricing in real time.
   - Linkedin uses kafka to collect user interaction to make better connection recommendations in real time.
   
# Terminologies
  <img src="https://github.com/eshita19/kafka/blob/master/kafka4.png"></img>
  ## Topic: 
   - A particular stream of data
   - We can have as many topics as we want.
   - A topic is identified by its name.
   - Topics are split into partitions. Each partitions has series of messages with each message at an incremental offset. Order of messages is guranteeed only within a partition.
   - For example : 
     - We are a truck company. Each truck report GPS position to kafka.
     - We can have a topic truck_gps that contains position of all trucks.
     - Each truck will send a message to kafka every 20 second, each msg will contain truck id and position.
     - Consumer of kafka can be many apps like : Location service(real time truck location monitoring) and Notification service(Service which reports if a truck is running to long).
     - Data in kafka is kept for at max one week, but offset are not reset, it keeps on incrementing.
     - Once data is written to a partition it cannot be changed.
     
  ## Kafka Cluster:
   - A kafka cluster consist of multiple brokers.
   - Each broker consist of topic partitions. Partitions from one topic are distributed among the brokers.
   - If we connect to one broker, we will be connected to all brokers.
   - Topic should have replication factor of atleast 2. The topic partition should be duplicated among brokers, so that if one broker goes down another broker can serve the topic partition.
   - <img src="https://github.com/eshita19/kafka/blob/master/kafka2.png"></img>
   
 ## Producer:
  - Producer sends data to kafka cluster.
  - Producer can choose to receive acknowledgement about data writes.
     - acks=0 : producer won't wait for ack(Possible data loss)
     - acks=1 : producer will wait for leader(broker)ack. That is the message was wriiten to leader broker.(limited data loss)
     - acks=2 : producer will wait for leader + replica broker ack.(no data loss)
  - The messages will be in order within a partition. But across multiple partition order is not guranteed.
     - If the producer doesn't provider key for messages, data will be sent to brokers in round robin fashion.
     - If the producer send key with message, message will be sent to particular broker using key hashing.

 ## Consumer:
  - Consumer reads data from broker by topic name.
  - Consumer knows broker to read from.
  - Consumer will read data from partitions of a topic in parallel, but within a partition, data will be read in order. 
  - **Consumer groups**: 
     - Each consumer within a group reads data from exclusive partition(1 or more but exclusive).
     - When a new consumer joins/removed from consumer group, the partitions are rea-assigned. 
  - **Consumer offsets**
    - Similar to git commit.
    - Kafka stores the offsets at which consumer group has been reading. It will be stored in a kafka topic by name `__consumer_offsets` 
    - This helps in resuming the reading of data from the last left offset read from consumer offset topic.
    - Consumer choose when to commit offsets:
     - *At most once* : Offset is committed as soon as message is read, even if the processing of message might have failed which could lead to data loss. Means that for each message handed to the mechanism, that message is delivered zero or one times; in more casual terms it means that messages may be lost.
     - *At least  once*:  Offsets are committed once message has been processed. If the processing goes wrong message will be read again. Means that for each message handed to the mechanism potentially multiple attempts are made at delivering it, such that at least one succeeds; again, in more casual terms this means that messages may be duplicated but not lost.
     - *Exactly once*: Means that for each message handed to the mechanism exactly one delivery is made to the recipient; the message can neither be lost nor duplicated.
     
  ## Kafka auto broker discovery:
   - Each kafka broker is named as bootstrap broker. Beacuse each broker has metadata of all other brokers,  whoch topic partition each broker hosts.
   - When a kafka client connects to a broker, apart from connection establishment, broker also sends metadata of all brokers, using which kafka client(producer/consumer) can communicate with appropriate broker.
   
 ## ZoooKeeper
   - Kafka can't work without zoookeeper.
   - Zookepper manages the brokers and keeps a list of them.
   - It notifys kafka, if a broker goes down, broker comes up, new topic, delete topic.
   - It helps in leader election for partitions.
   - The number of zookeeper servers has to be odd number. There can be either 1,3,5,7..
   - <img src="https://github.com/eshita19/kafka/blob/master/kafka3.png"></img>
   
 ## Starting Kafka:
  - Download apache-kafka. Add apache-kafka/bin to Path.
  - Start zookeper: `zookeeper-server-start.sh config/zookeeper.properties`
  - Start Kafka: `kafka-server-start.sh config/server.properties`
  - **Topic CLIs**:
      - Create topic: `kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 2`
      - List topics: `kafka-topics.sh --zookeeper 127.0.0.1:2181 --list`
      - Description of a topic: `kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe`
      - Delete topic: `kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --delete`
  - **Producer CLIs**:
      - Push messages to an existing/new topic. If the topic doesn't exist, it will be created with default log partitions from server.properties. `--broker-list` is the IP and port of kafka broker : `kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic`.
  - **Consumer CLIs**:
      - Consumer message from a topic. `--bootstrap-server` is the kafka broker. `kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic`. This will read messages live. 
       - To read messages from beginning add option: `--from beginning`.
       - To define consumer group: `--group 1`.
  - **Consumer Group CLIs**:
     - See all the consumers within a consumer group: `kafka-consumer-group.sh --bootrap-server localhost:9092 --group group1 --describe`
  - **Resetting offsets**:
    - `kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group group1 --topic first_topic --reset-offsets --to-earliest`
    - Option `--shift-by` : Left shift(-) or right shift the offset in each partition. 
    

## Twitter application:
- https://developer.twitter.com/en/apps.
- Twitter client: https://github.com/twitter/hbc.


## Interview Questions:
- https://data-flair.training/blogs/kafka-interview-questions/


      
   
   
 
 
   
     
     
     
   
 
 
 
 
