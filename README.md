# fogTest
Top 10 words from stream using flink-kafka

docker pull davaa0910/flinktest to run docker image


A fog node was designed as a single flink producer written in Java, which reads words from words.txt file, and pushes one random word in every 100ms to kafka stream.

Words.txt file includes most popular 1000 English words.

Flink producer and text file are included in same directory. As for the scalability we can manage stream producers by starting another task in flink platform.

Central unit includes kafka streaming platform, flink consumer, and web page.

Kafka streaming is running only 1 topic, and 1 partition.

Flink consumer written in java, which pulls data from kafka, analyzes, and writes result of top 10 words with occurrences to single text file.

A web page, written in php, is working on an apache server, which displays the contents of the text file in every 3 seconds.

All components along with the base Ubuntu image later wrapped into one docker image.
