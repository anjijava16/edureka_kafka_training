package com.edureka.co.listener;

import com.edureka.co.util.PropertiesLoader;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author  sagar.akshayan
 */
public class ConsumerListener{

    ConsumerConnector consumerConnector;

    public ConsumerListener() {
        Properties props= PropertiesLoader.getKafkaProperties("test");
        consumerConnector = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
    }


    public LinkedBlockingQueue<String> consume(String topic) {
        LinkedBlockingQueue<String> result = new LinkedBlockingQueue<>();
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
         KafkaStream stream = streams.get(0);
        listenToProducer(result,stream);
        return result;

    }

    private void listenToProducer( LinkedBlockingQueue<String> result,  KafkaStream stream) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
                public void run() {
                    ConsumerIterator<byte[], byte[]> it = stream.iterator();
                    while (it.hasNext()) {
                        String msg = new String(it.next().message());
                        result.offer(msg);
                    }

                }
            });
    }
    
}
