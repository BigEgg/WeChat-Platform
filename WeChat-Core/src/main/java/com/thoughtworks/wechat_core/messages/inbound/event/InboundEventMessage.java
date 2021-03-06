package com.thoughtworks.wechat_core.messages.inbound.event;

import com.thoughtworks.wechat_core.messages.inbound.InboundMessage;
import com.thoughtworks.wechat_core.messages.inbound.InboundMessageType;
import org.joda.time.DateTime;

public abstract class InboundEventMessage implements InboundMessage {
    private InboundMessageType messageType = InboundMessageType.EVENT;
    private DateTime createdTime;
    private EventType eventType;

    public InboundEventMessage(final DateTime createdTime, final EventType eventType) {
        this.createdTime = createdTime;
        this.eventType = eventType;
    }

    @Override
    public InboundMessageType getMessageType() {
        return messageType;
    }

    @Override
    public DateTime getCreatedTime() {
        return createdTime;
    }

    public EventType getEventType() {
        return eventType;
    }
}
