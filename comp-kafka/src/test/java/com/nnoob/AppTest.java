package com.nnoob;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static junit.framework.TestCase.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }


    public Properties initConfig() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return properties;

    }


    @Test
    public void testConnect() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(initConfig());

        consumer.subscribe(Collections.singletonList("test"));


        List<PartitionInfo> test = consumer.partitionsFor("test");

        List<TopicPartition> partitionInfos = new ArrayList<>();
        for (PartitionInfo partitionInfo : test) {

            partitionInfos.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
        }

        consumer.assign(partitionInfos);

        ConsumerRecords<String, String> poll = consumer.poll(Duration.ofSeconds(3));

        for (ConsumerRecord<String, String> stringStringConsumerRecord : poll) {
            String value = stringStringConsumerRecord.value();

            System.err.println(value);
        }


    }
}
