package uk.nhs.prm.deductions.gp2gpmessagehandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class Gp2gpMessageHandlerApplicationTests {
	@Autowired
	JmsTemplate jmsTemplate;

	@Value("${activemq.inboundQueue}")
	private String inboundQueue;

	@Value("${activemq.outboundQueue}")
	private String outboundQueue;

	@Test
	void shouldPassThroughMessages() {
		//action: send a message on the inbound q
		String testMessage = "test message";
		jmsTemplate.convertAndSend(inboundQueue, testMessage);

		//assertion: verify the message gets on the outbound q
		jmsTemplate.setReceiveTimeout(5000);
		Message message = jmsTemplate.receive(outboundQueue);
		assertNotNull(message);
		TextMessage textMessage = (TextMessage) message;
		try {
			assertThat(textMessage.getText(), equalTo(testMessage));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}