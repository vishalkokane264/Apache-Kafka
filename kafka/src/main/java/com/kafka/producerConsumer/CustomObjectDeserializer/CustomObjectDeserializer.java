package com.kafka.producerConsumer.CustomObjectDeserializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.kafka.producerConsumer.CustomObject.CustomObject;
import com.kafka.producerConsumer.ObjectMapper.ObjectMapper;

public class CustomObjectDeserializer implements Deserializer<CustomObject> {
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public CustomObject deserialize(String topic, byte[] data) {
		ObjectMapper mapper = new ObjectMapper();
		CustomObject object = null;
		try {
			object = mapper.readValue(data, CustomObject.class);
		} catch (Exception exception) {
			System.out.println("Error in deserializing bytes " + exception);
		}
		return object;
	}

	@Override
	public void close() {
	}
}