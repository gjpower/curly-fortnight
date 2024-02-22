package me.gjpower.jsonassignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.gjpower.jsonassignment.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JsonassignmentApplication {

	@Value ("${storage.file:storage.json}")
	private String storageFileLocation;
	@Value ("${event.log.file:event-log.log}")
	private String logFileLocation;

	public static void main(String[] args) {
		SpringApplication.run(JsonassignmentApplication.class, args);
	}

	@Bean
	TransactionStore store(TransactionBackend backend, TransactionUpdateMessenger updateMessenger) {
		return new TransactionFileStore(backend, updateMessenger);
	}

	@Bean
	TransactionBackend backend(ObjectMapper om) {
		return new TransactionFileBackend(om, storageFileLocation);
	}

	@Bean
	TransactionUpdateMessenger updateMessenger() {
		return new TransactionUpdateFileMessenger(logFileLocation);
	}
}
