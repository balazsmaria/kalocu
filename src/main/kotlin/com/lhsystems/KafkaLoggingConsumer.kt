package com.lhsystems

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.util.*


class KafkaLoggingConsumer(bootstrapServer: String, topics: List<String>) {

    private val logger = LoggerFactory.getLogger(KafkaLoggingConsumer::class.java.name)

    private val bootstrapServer = bootstrapServer
    private val topics = topics

    private fun consume() {

        logger.info("Consuming started")

        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
            put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString())
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
            put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString())
            put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
        }
        val consumer = KafkaConsumer<String, String>(props)
        consumer.subscribe(topics)
        while (true) {
            val records = consumer.poll(100)
            for (record in records)
                logger.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value())
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val bootstrapServers: String = System.getenv("BOOTSTRAP_SERVERS")
            val topics: String = System.getenv("TOPICS")

            val consumer = KafkaLoggingConsumer(bootstrapServers, topics.split(","))
            consumer.consume()
        }
    }
}