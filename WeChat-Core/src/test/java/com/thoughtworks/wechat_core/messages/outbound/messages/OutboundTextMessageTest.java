package com.thoughtworks.wechat_core.messages.outbound.messages;

import com.thoughtworks.wechat_core.messages.outbound.OutboundMessageEnvelop;
import com.thoughtworks.wechat_core.messages.outbound.OutboundMessageType;
import com.thoughtworks.wechat_core.wechat.outbound.WeChatOutboundTextMessage;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.util.Optional;

import static com.thoughtworks.wechat_core.util.xstream.XStreamExtension.createXStreamWithCData;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class OutboundTextMessageTest {
    @Test
    public void testConstructor() throws Exception {
        final OutboundTextMessage textMessage = new OutboundTextMessage("content");
        assertThat(textMessage.getMessageType(), equalTo(OutboundMessageType.TEXT));
        assertThat(textMessage.getCreatedTime(), notNullValue());
        assertThat(textMessage.getContent(), equalTo("content"));
    }

    @Test
    public void testToWeChat() throws Exception {
        final OutboundTextMessage textMessage = new OutboundTextMessage("content");
        final OutboundMessageEnvelop envelop = new OutboundMessageEnvelop("fromUser", "toUser", Optional.of(textMessage));

        final WeChatOutboundTextMessage weChatOutboundTextMessage = (WeChatOutboundTextMessage) textMessage.toWeChat(envelop);
        final XStream xStream = createXStreamWithCData();
        xStream.processAnnotations(WeChatOutboundTextMessage.class);
        final String xmlMessage = xStream.toXML(weChatOutboundTextMessage);

        assertThat(xmlMessage.contains("<xml>"), is(true));
        assertThat(xmlMessage.contains("<ToUserName><![CDATA[toUser]]></ToUserName>"), is(true));
        assertThat(xmlMessage.contains("<FromUserName><![CDATA[fromUser]]></FromUserName>"), is(true));
        assertThat(xmlMessage.contains("<CreateTime>"), is(true));
        assertThat(xmlMessage.contains("</CreateTime>"), is(true));
        assertThat(xmlMessage.contains("<MsgType><![CDATA[text]]></MsgType>"), is(true));
        assertThat(xmlMessage.contains("<Content><![CDATA[content]]></Content>"), is(true));
        assertThat(xmlMessage.contains("</xml>"), is(true));
    }
}