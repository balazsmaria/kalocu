package com.lhsystems

import ch.qos.logback.classic.LoggerContext
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import org.testcontainers.containers.KafkaContainer
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


internal class KafkaLoggingConsumerTest {

    private val logger = LoggerFactory.getLogger(KafkaLoggingConsumerTest::class.java.name)

    private val topic = "test_topic"
    private val kafka = KafkaContainer()

    private fun bootstrapServers(): String {
        return kafka.bootstrapServers.replace("PLAINTEXT://", "")
    }

    @Before
    fun before() {
        kafka.start()
    }

    @After
    fun after() {
        kafka.stop()
    }

    @Test
    fun testConsumer() {

        val lc = LoggerFactory.getILoggerFactory() as LoggerContext
        val listAppender = ListAppender()
        listAppender.context = lc
        listAppender.start()

        lc.getLogger(KafkaLoggingConsumer::class.java.name).addAppender(listAppender)

        Executors.newSingleThreadExecutor().submit(this::produce)

        logger.info("Bootstrap servers: {}", bootstrapServers())
        val consumer = KafkaLoggingConsumer(bootstrapServers(), listOf(topic))
        Executors.newSingleThreadExecutor().submit(consumer::consume)

        await().atMost(1, TimeUnit.MINUTES).until { listAppender.events.size > 25 }
        assertTrue(listAppender.events.get(25).contains(Regex("offset = ([0-9][0-9]), key = ([0-9][0-9]), value = ([0-9][0-9])")))
    }

    private fun produce() {
        TimeUnit.SECONDS.sleep(5)
        val props = Properties().apply {
            put("bootstrap.servers", bootstrapServers())
            put("acks", "all")
            put("retries", 10)
            put("batch.size", 16384)
            put("linger.ms", 1)
            put("buffer.memory", 33554432)
            put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
            put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        }

        val producer = KafkaProducer<String, String>(props)
        for (i in 0..99) {
            producer.send(ProducerRecord(topic, Integer.toString(i), Integer.toString(i)))
        }
        producer.close()
    }

    private fun newUserIsAdded(listAppender: ListAppender): Callable<Boolean> {
        return Callable {
            listAppender.events.size == 50 // The condition that must be fulfilled
        }
    }
}

