package com.lhsystems;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class ListAppender extends AppenderBase<ILoggingEvent> {

    public final List<String> events = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
        events.add(event.getFormattedMessage());
    }
}