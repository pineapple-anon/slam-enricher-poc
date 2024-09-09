package com.demo.slamenricher.tmp;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class DbWriter implements MessageHandler {

    @ServiceActivator(inputChannel = "jdbcWriterChannel", outputChannel = "nullChannel")
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        // write to db
        acknowledgeKafka(message);
    }

    private void acknowledgeKafka(Message<?> message) {
        Acknowledgment ack = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
        if (ack != null) {
            ack.acknowledge();
        }
    }
}
