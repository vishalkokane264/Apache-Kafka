package com.kafka.producerConsumer.CustomSeralizer;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.kafka.producerConsumer.CustomObject.CustomObject;
import com.kafka.producerConsumer.ObjectMapper.ObjectMapper;

public class CustomSeralizer implements Serializer<CustomObject> {

	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	public byte[] serialize(String topic, CustomObject data) {
		byte[] retVal = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			retVal = objectMapper.writeValueAsString(data).getBytes();
		} catch (Exception exception) {
			System.out.println("Error in serializing object" + data);
		}
		return retVal;
	}

	public void close() {

	}

}