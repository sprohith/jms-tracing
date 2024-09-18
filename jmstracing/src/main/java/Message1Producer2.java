import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
//import javax.jms.*;

import io.opentracing.Tracer;
import io.opentracing.contrib.jms2.TracingMessageProducer;
import io.opentracing.util.GlobalTracer;

public class Message1Producer2 {
	public static void main(String[] args) throws Exception {
		
		// Instantiate tracer
		Tracer tracer = GlobalTracer.get();

        // Connection factory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Connection   and session
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


        // Destination (queue    or topic)
        Queue destination = session.createQueue("myQueue");

        // Message producer
        MessageProducer messageProducer = session.createProducer(destination);
        TracingMessageProducer producer = new TracingMessageProducer(messageProducer, tracer);

        // Create and send a message
        TextMessage message = session.createTextMessage("Hello Artemis Tracer!");
        producer.send(message);

        // Close resources
        producer.close();
        session.close();
        connection.close();
        System.out.println("Done");
    }
}