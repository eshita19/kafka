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
     - Topics are split into partitions. Each partitions has series of messages with each message at an incremental offset.
     - For example : 
       - We are a truck company. Each truck report GPS position to kafka.
       - We can have a topic truck_gps that contains position of all trucks.
       - Each truck will send a message to kafka every 20 second, each msg will contain truck id and position.
       - Consumer of kafka can be many apps like : Location service(real time truck location monitoring) and Notification service(Service which reports if a truck is running to long).
     
   
 
 
 
 
