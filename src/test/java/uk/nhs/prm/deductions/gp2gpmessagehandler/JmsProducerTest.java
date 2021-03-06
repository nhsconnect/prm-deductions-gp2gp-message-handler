package uk.nhs.prm.deductions.gp2gpmessagehandler;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.junit.jupiter.api.*;
import javax.jms.JMSException;

import org.mockito.*;
import org.springframework.jms.core.JmsTemplate;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Tag("unit")
public class JmsProducerTest {
    @Mock
    JmsTemplate jmsTemplate;
    private AutoCloseable closeable;

    @InjectMocks
    JmsProducer jmsProducer;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Captor
    ArgumentCaptor<ActiveMQBytesMessage> bytesMessageArgumentCaptor;

    @Test
    void shouldSendMessageToQueue() throws JMSException {
        String expectedMessage = "test";
        String queueName = "test-queue";

        jmsProducer.sendMessageToQueue(queueName, expectedMessage);
        verify(jmsTemplate).convertAndSend(eq(queueName), bytesMessageArgumentCaptor.capture());

        ActiveMQBytesMessage bytesMessage = bytesMessageArgumentCaptor.getValue();
        byte[] contentAsBytes = new byte[(int) bytesMessage.getBodyLength()];
        bytesMessage.readBytes(contentAsBytes);
        String actualMessage = new String(contentAsBytes, StandardCharsets.UTF_8);

        assertEquals(expectedMessage, actualMessage);
    }
}
