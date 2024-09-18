import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import io.opentracing.Tracer;
import io.opentracing.contrib.jms.common.TracingMessageConsumer;
import io.opentracing.contrib.jms.common.TracingMessageListener;
import io.opentracing.util.GlobalTracer;

import java.util.Iterator;

import javax.jms.*;

public class Message1Consumer2 {
    public static void main(String[] args) throws Exception {
        try (// Connection factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")) {
        	
        	Tracer tracer = GlobalTracer.get();
        	
			// Connection and session
			Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Destination (queue or topic)
			Destination destination = session.createQueue("myQueue");

			// Message consumer
			MessageConsumer messageConsumer = session.createConsumer(destination);
			TracingMessageConsumer consumer = new TracingMessageConsumer(messageConsumer, tracer);
		
			MessageListener messageListener = new MessageListener() {
			    @Override
			    public void onMessage(Message message) {
			    	try {
						Iterator x = message.getPropertyNames().asIterator();
						while(x.hasNext()) {
							System.out.println(x.next());
						}
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			        try {
			        	System.out.println(message.getIntProperty("JMSXDeliveryCount"));
			            if (message instanceof TextMessage) {
			                TextMessage textMessage = (TextMessage) message;
			                System.out.println("Received message Tracer: " + textMessage.getText());
			            }
			        } catch (JMSException e) {
			            e.printStackTrace();
			        }
			    }
			};
			
			TracingMessageListener listener = new TracingMessageListener(messageListener, tracer);
			
			// Message listener
			consumer.setMessageListener(listener);

			// Keep the consumer running indefinitely
			synchronized (consumer) {
			    consumer.wait();
			}
		}
    }
}