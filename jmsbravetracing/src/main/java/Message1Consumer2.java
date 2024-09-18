import java.util.Iterator;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import brave.Tracing;
import brave.jakarta.jms.JmsTracing;
import brave.messaging.MessagingTracing;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class Message1Consumer2 {
    public static void main(String[] args) throws Exception {
        try (// Connection factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")) {
        	
        	Tracing tracing = Tracing.newBuilder()
                    .localServiceName("my-service")
                    //.addSpanHandler(zipkinSpanHandler)
                    .build();
        	
        	MessagingTracing messageTracing = MessagingTracing.newBuilder(tracing).build();
        	JmsTracing jmsTracing = JmsTracing.newBuilder(messageTracing)
                    .remoteServiceName("my-broker")
                    .build();
        	
        	ConnectionFactory tracingConnectionFactory = jmsTracing.connectionFactory(connectionFactory);
        	
			// Connection and session
			Connection connection = tracingConnectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Destination (queue or topic)
			Queue destination = session.createQueue("myQueue");

			// Message consumer
			MessageConsumer consumer = session.createConsumer(destination);
			
			// Message listener
			consumer.setMessageListener(new MessageListener() {
			    @Override
			    public void onMessage(Message message) {
			        try {
			        	try {
							Iterator x = message.getPropertyNames().asIterator();
							while(x.hasNext()) {
								System.out.println(x.next());
							}
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            if (message instanceof TextMessage) {
			                TextMessage textMessage = (TextMessage) message;
			                System.out.println("Received message: " + textMessage.getText());
			            }
			        } catch (JMSException e) {
			            e.printStackTrace();
			        }
			    }
			});

			// Keep the consumer running indefinitely
			synchronized (consumer) {
			    consumer.wait();
			}
		}
    }
}