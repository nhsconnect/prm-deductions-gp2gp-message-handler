package uk.nhs.prm.deductions.gp2gpmessagehandler;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import uk.nhs.prm.deductions.gp2gpmessagehandler.utils.TestDataLoader;

import javax.jms.JMSException;

import java.io.IOException;

import static org.mockito.Mockito.*;

/*
 Tests JMS Consumer together with queues
 */
@Tag("unit")
public class JmsConsumerIntegrationTest {
    @Mock
    JmsProducer jmsProducer;

    @Value("${activemq.unhandledQueue}")
    String unhandledQueue;
    @Value("${activemq.inboundQueue}")
    String inboundQueue;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @InjectMocks
    JmsConsumer jmsConsumer;

    private TestDataLoader dataLoader = new TestDataLoader();

    private ActiveMQBytesMessage getActiveMQBytesMessage(byte[] bytes) throws JMSException {
        ActiveMQBytesMessage message = new ActiveMQBytesMessage();
        message.writeBytes(bytes);
        message.reset();
        return message;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "simpleTextMessage.txt",
            "RCMR_IN030000UK06WithoutInteractionId",
            "RCMR_IN030000UK06WithoutMessageHeader",
            "RCMR_IN030000UK06WithoutSoapHeader",
            "RCMR_IN030000UK06WithIncorrectInteractionId"
    })
    void shouldSendMessageToUnhandledQueue(String fileName) throws JMSException, IOException {
        byte[] bytes = dataLoader.getDataAsBytes(fileName);
        String message = dataLoader.getDataAsString(fileName);

        ActiveMQBytesMessage bytesMessage = getActiveMQBytesMessage(bytes);
        jmsConsumer.onMessage(bytesMessage);
        verify(jmsProducer, times(1)).sendMessageToQueue(ArgumentMatchers.eq(unhandledQueue), ArgumentMatchers.eq(message));
    }
}
