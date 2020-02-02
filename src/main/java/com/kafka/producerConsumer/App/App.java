package com.kafka.producerConsumer.App;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.kafka.producerConsumer.IKafkaConstants.IKafkaConstants;
import com.kafka.producerConsumer.createConsumer.ConsumerCreator;
import com.kafka.producerConsumer.producerCreator.producerCreator;

public class App {
    public static void main(String[] args) throws Exception
    {
    	runProducer();
    	runConsumer();        
    }
	static void runConsumer() {
		Consumer<Long, String> consumer = ConsumerCreator.createConsumer();

		int noMessageToFetch = 0;

		while (true) {
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			if (consumerRecords.count() == 0) {
				noMessageToFetch++;
				if (noMessageToFetch > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					break;
				else
					continue;
			}

			consumerRecords.forEach(record -> {
				System.out.println("!value " + record.value());
			});
			consumer.commitAsync();
		}
		consumer.close();
	}
    
	static void runProducer() throws IOException {
		Producer<Long, String> producer = producerCreator.createProducer();
		String csvFile="/home/vishal/kafka/src/main/java/com/kafka/producerConsumer/App/data.csv";
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
    			final ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_NAME,
    					"{col0:" + data[0]+",col2:"+data[1]+",col3:"+data[2]+",col3:"+data[3]+"}");
    			try {
    				RecordMetadata metadata = producer.send(record).get();
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
