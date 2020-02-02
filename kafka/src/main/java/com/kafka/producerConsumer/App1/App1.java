package com.kafka.producerConsumer.App1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import com.kafka.producerConsumer.IKafkaConstants.IKafkaConstants;
import com.kafka.producerConsumer.KStreamBuilder.KStreamBuilder;
import com.kafka.producerConsumer.createConsumer.ConsumerCreator;
import com.kafka.producerConsumer.producerCreator.producerCreator;

public class App1 {
	  static final String topicName = "topic2";
    public static void main(String[] args) throws Exception
    {

		Consumer<Long, String> consumer2 = ConsumerCreator.createConsumer("text-output");
		Consumer<Long, String> consumer3 = ConsumerCreator.createConsumer("text-output");

		runProducer();
		KstreamBuilderFunc("topic2");
    	runConsumer(consumer2,"Output2.txt");    
//    	runConsumer(consumer3,"Output3.txt");
    }
    
	private static void KstreamBuilderFunc(String oTopic2) throws InterruptedException {
	    Properties prop = new Properties();
	    prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
	    prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
	    prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		StreamsBuilder builder =new StreamsBuilder();
//		StreamsBuilder builder2=new StreamsBuilder();
		builder.stream(oTopic2).to("text-output");
//		builder2.stream(oTopic2).to("text-output2");
		
		KafkaStreams streams=new KafkaStreams(builder.build(),prop);
//		KafkaStreams streams2=new KafkaStreams(builder2.build(),prop);

		streams.start();
		Thread.sleep(3000);
		streams.close();
		System.out.println("HEllo2");
		
//		streams2.start();		
//		Thread.sleep(3000);
//		streams2.close();
//		System.out.println("HEllo2");
		
	}

	static void runConsumer(Consumer<Long, String> consumer,String file) throws IOException {

		int noMessageToFetch = 0;
		BufferedWriter writer=new BufferedWriter(new FileWriter(file));

		while (true) {
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(10);
			if (consumerRecords.count() == 0) {
				noMessageToFetch++;
				if (noMessageToFetch > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					break;
				else
					continue;
			}
			consumerRecords.forEach(record -> {
				try {
					writer.write(record.value()+'\n');
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(record.value());
				
			});
			consumer.commitAsync();
		}
		writer.close();
		consumer.close();
	}
    
	static void runProducer() throws IOException {
		Producer<Long, String> producer = producerCreator.createProducer();
		String csvFile="/home/vishal/Downloads/kafka/kafka/src/main/java/com/kafka/producerConsumer/App/data.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int index=0;
        try
        {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null)
            {
            	index++;
                String[] data = line.split(cvsSplitBy);
    			final ProducerRecord<Long, String> record = new ProducerRecord<Long, String>("topic2",
    					"{col0:" + data[0]+",col1:"+data[1]+",col2:"+data[2]+",col3:"+data[3]+"}");
    			
    			try {
    				RecordMetadata metadata = producer.send(record).get();    				
    				System.out.println("Data send from producer to topic2");
    				metadata.partition();
    				metadata.offset();
    	
    			} catch (ExecutionException e) {
    				System.out.println("Error in sending record");
    				System.out.println(e);
    			} catch (InterruptedException e) {
    				System.out.println("Error in sending record");
    				System.out.println(e);
    			}
    			
            }
            
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

	}
}
