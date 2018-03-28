# kalocu

Overview
---

The application reads one or more Kafka topics and logs the received messages to the console.

Usage:
---
To start call `docker-compose up`

Configuration
---
Via environmental variables (modify in the compose file):

- BOOTSTRAP_SERVERS: the url where Kafka is listening
- TOPICS: the topics to consume
