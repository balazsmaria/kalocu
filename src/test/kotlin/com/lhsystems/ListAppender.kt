package com.lhsystems

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

import java.util.ArrayList

class ListAppender : AppenderBase<ILoggingEvent>() {

    val events: MutableList<String> = ArrayList()

    override fun append(event: ILoggingEvent) {
        events.add(event.formattedMessage)
    }
}