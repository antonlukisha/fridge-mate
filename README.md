cd C:\Program Files\Redis
.\redis-server.exe

cd C:\Kafka\kafka_2.13-3.8.1
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

cd C:\Kafka\kafka_2.13-3.8.1
.\bin\windows\kafka-server-start.bat .\config\server.properties
