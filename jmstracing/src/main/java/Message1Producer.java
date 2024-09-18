import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
//import javax.jms.*;

public class Message1Producer {
	public static void main(String[] args) throws Exception {
        // Connection factory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Connection   and session
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


        // Destination (queue    or topic)
        Queue destination = session.createQueue("myQueue");

        // Message producer
        MessageProducer producer = session.createProducer(destination);

        // Create and send a message
        TextMessage message = session.createTextMessage("Hello Artemis!");
        producer.send(message);

        // Close resources
        producer.close();
        session.close();
        connection.close();
        System.out.println("Done");
    }
}