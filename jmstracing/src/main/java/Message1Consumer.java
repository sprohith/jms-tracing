import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import javax.jms.*;

public class Message1Consumer {
    public static void main(String[] args) throws Exception {
        try (// Connection factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")) {
			// Connection and session
			Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Destination (queue or topic)
			Destination destination = session.createQueue("myQueue");

			// Message consumer
			MessageConsumer consumer = session.createConsumer(destination);

			// Message listener
			consumer.setMessageListener(new MessageListener() {
			    @Override
			    public void onMessage(Message message) {
			        try {
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