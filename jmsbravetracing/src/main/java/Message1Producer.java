
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
//import javax.jms.*;

import jakarta.jms.Connection;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class Message1Producer {
	public static void main(String[] args) throws Exception {
        try (// Connection factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")) {
			// Connection   and session
			Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


			// Destination (queue    or topic)
			Queue destination = session.createQueue("myQueue");

			// Message producer
			MessageProducer producer = session.createProducer(destination);

			// Create and send a message
			TextMessage message = session.createTextMessage("Hello Artemis Zipkin Tracing!");
			producer.send(message);

			// Close resources
			producer.close();
			session.close();
			connection.close();
		}

        System.out.println("Done");
    }
}