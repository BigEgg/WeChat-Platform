package com.thoughtworks.wechat_application.logic.workflow.subscribe;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thoughtworks.wechat_application.logic.workflow.Workflow;
import com.thoughtworks.wechat_application.logic.workflow.WorkflowLevel;
import com.thoughtworks.wechat_application.logic.workflow.WorkflowLevelAnnotation;
import com.thoughtworks.wechat_application.logic.workflow.subscribe.steps.SubscribeWorkflowStep;
import com.thoughtworks.wechat_core.messages.inbound.InboundMessage;
import com.thoughtworks.wechat_core.messages.inbound.InboundMessageEnvelop;
import com.thoughtworks.wechat_core.messages.inbound.event.InboundSubscribeEvent;
import com.thoughtworks.wechat_core.wechat.inbound.event.WeChatSubscribeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SubscribeWorkflowTest {
    @Mock
    private SubscribeWorkflowStep subscribeWorkflowStep;
    private SubscribeWorkflow workflow;
    private Injector injector;

    @Before
    public void setUp() throws Exception {
        injector = Guice.createInjector(binder -> {
            binder.bind(SubscribeWorkflowStep.class).toInstance(subscribeWorkflowStep);
        });

        workflow = injector.getInstance(SubscribeWorkflow.class);
    }

    @Test
    public void testWorkflowLevel() throws Exception {
        assertThat(workflow.getClass().isAnnotationPresent(WorkflowLevelAnnotation.class), equalTo(true));
        assertThat(workflow.getClass().getAnnotation(WorkflowLevelAnnotation.class).level(), equalTo(WorkflowLevel.SPECIFIC));
    }

    @Test
    public void testInject_Singleton() throws Exception {
        final Workflow anotherWorkflow = injector.getInstance(SubscribeWorkflow.class);
        assertThat(workflow, equalTo(anotherWorkflow));
    }

    @Test
    public void testCanStartHandle_SubscribeEvent() throws Exception {
        final boolean canHandle = workflow.canStartHandle(createSubscribeEventEnvelop());

        assertThat(canHandle, equalTo(true));
    }

    @Test
    public void testCanStartHandle_Not_SubscribeEvent() throws Exception {
        final boolean canHandle = workflow.canStartHandle(new InboundMessageEnvelop("fromUser", "toUser", mock(InboundMessage.class)));

        assertThat(canHandle, equalTo(false));
    }

    private InboundMessageEnvelop createSubscribeEventEnvelop() {
        final WeChatSubscribeEvent event = new WeChatSubscribeEvent("toUser", "fromUser", 1422800623, "event", "subscribe");
        final InboundSubscribeEvent inboundSubscribeEvent = new InboundSubscribeEvent(event);

        return new InboundMessageEnvelop("fromUser", "toUser", inboundSubscribeEvent);
    }
}