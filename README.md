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
  - 
     
     
   
 
 
 
 
