package com.demo.slamenricher.tmp;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaToParallelDbPipelineConfig {

    private final CustomMessageProcessor messageProcessor;
    private final DbWriter dbWriter;

    public KafkaToParallelDbPipelineConfig(CustomMessageProcessor messageProcessor, DbWriter dbWriter) {
        this.messageProcessor = messageProcessor;
        this.dbWriter = dbWriter;
    }

    @Bean
    public IntegrationFlow kafkaToParallelDbFlow(
            ConsumerFactory<String, String> consumerFactory,
            TaskExecutor taskExecutor1) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(
                                consumerFactory,
                                KafkaMessageDrivenChannelAdapter.ListenerMode.record,
                                "your-topic-name")
                        .configureListenerContainer(c ->
                                c.ackMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE))
                        .id("kafkaListener"))
                .channel("processChannel")
                .handle(messageProcessor::handleMessage)
                .channel(c -> c.executor(taskExecutor1))
                .split()
                .channel("jdbcWriterChannel")
                .get();
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "your-group-id");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public TaskExecutor taskExecutor1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("dbWriter-");
        executor.initialize();
        return executor;
    }

}
