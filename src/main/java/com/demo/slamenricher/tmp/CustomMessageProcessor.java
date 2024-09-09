package com.demo.slamenricher.tmp;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class CustomMessageProcessor {

    public void processMsg(Message<?> message) throws MessagingException {

    }
}
