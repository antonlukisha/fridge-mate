<<<<<<< HEAD
cd C:\Program Files\Redis
.\redis-server.exe

cd C:\Kafka\kafka_2.13-3.8.1
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

cd C:\Kafka\kafka_2.13-3.8.1
.\bin\windows\kafka-server-start.bat .\config\server.properties
=======
# FridgeMate


[About](#dart-about) | [Description](#pencil2-description) | [Algorithm](#triangular_ruler-algorithm) | [Bash Example](#paperclip-bash-example)


## :dart: About

FridgeMate is a web-based application (with potential integration to mobile platforms) designed to serve as a virtual fridge for students. It helps users manage their food inventory, track expiration dates, and make efficient use of ingredients. The project uses a microservice architecture and includes a neural network-based recommendation system for suggesting recipes.

## :pencil2: Description

The application consists of a frontend developed in React and a backend built with Java Spring Boot. It also integrates Python (PyTorch) for neural network operations to provide personalized recipe recommendations based on the food items in the user's fridge. Each working directory of the project includes a dedicated README file with a detailed explanation of its structure.

## :paperclip: Bash Example

For run of this application project directory has `docker-compose.yml` which run all services, ai-part, front-end and another dependences.
>>>>>>> 587049ec8211f72286141db19c1ed420ff9820a0
