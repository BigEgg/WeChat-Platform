package com.thoughtworks.wechat_io.wechat.outbound;

public class OutboundMessageEnvelop {
    private String fromUser;
    private String toUser;
    private OutboundMessage message;

    public OutboundMessageEnvelop(String fromUser, String toUser, OutboundMessage message) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
    }
}
