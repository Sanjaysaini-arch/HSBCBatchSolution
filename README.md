# HSBCBatchSolution
HSBC ASKED ME TO CREATE A Microservices SOLUTION AROUND BATCH PROCESSING .
Project covers how to use Spring Batch with Spring Kafka to Publish JSON/String message to a Kafka topic

this microbatch service using Exceutor service crating multithreads and each thread read ,validate and stream  a file on kafka.

Before starting the application setup the kafka following are steps.



Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties
Start Kafka Server
bin/kafka-server-start.sh config/server.properties
Create Kafka Topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic downstream1
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic downstream2


once kafka up and running with created topics.
we can start the application as spring boot.
As it will start. will start reading validating and streaming the files inside resource folder.


application is ready with one round of testing , scope of improvements are there.
like configuration needs to move to config.. Junit need to add to cover all positive and negative scenarios.
which i am working cureently.


