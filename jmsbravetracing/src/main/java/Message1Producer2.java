
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
//import javax.jms.*;

import brave.Tracing;
import brave.jakarta.jms.JmsTracing;
import brave.messaging.MessagingTracing;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class Message1Producer2 {
	public static void main(String[] args) throws Exception {
        try (// Connection factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")) {
        	
        	// Configure a reporter, which controls how often spans are sent
       //   (this dependency is io.zipkin.reporter2:zipkin-sender-okhttp3)
       //sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
       //   (this dependency is io.zipkin.reporter2:zipkin-reporter-brave)
       //zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender);
       
        	Tracing tracing = Tracing.newBuilder()
                    .localServiceName("my-service")
                    //.addSpanHandler(zipkinSpanHandler)
                    .build();
        	
        	MessagingTracing messageTracing = MessagingTracing.newBuilder(tracing).build();
        	JmsTracing jmsTracing = JmsTracing.newBuilder(messageTracing)
                    .remoteServiceName("my-broker")
                    .build();
        	
        	ConnectionFactory tracingConnectionFactory = jmsTracing.connectionFactory(connectionFactory);
			// Connection   and session
			Connection connection = tracingConnectionFactory.createConnection();
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
		}

        System.out.println("Done");
    }
}