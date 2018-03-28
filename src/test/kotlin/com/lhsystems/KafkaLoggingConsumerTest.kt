package com.lhsystems

import org.junit.Test

internal class KafkaLoggingConsumerTest {

    @Test
    fun testConsumer(){
        val consumer = KafkaLoggingConsumer("localhost:9092", listOf("raw_sales"))
//        consumer.consume()
    }
}

