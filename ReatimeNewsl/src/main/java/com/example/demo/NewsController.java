package com.example.demo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class NewsController {

	
	public List<SseEmitter> emitters=new CopyOnWriteArrayList<>();
	
	
	//Method for Client Subscription
	@CrossOrigin
	@RequestMapping(value="subscribe", consumes=MediaType.ALL_VALUE)
	public SseEmitter subscribe() {
		SseEmitter sseEmitter=new SseEmitter(Long.MAX_VALUE);
		
		try {
			sseEmitter.send(SseEmitter.event().name("INIT"));
			System.out.println(sseEmitter);
			System.out.println(sseEmitter.toString());
			
			//Creating the ObjectMapper object
		      ObjectMapper mapper = new ObjectMapper();
		      //Converting the Object to JSONString
		      String jsonString = mapper.writeValueAsString(sseEmitter);
		      System.out.println(jsonString);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		sseEmitter.onCompletion( () -> emitters.remove(sseEmitter));
		emitters.add(sseEmitter);
		return sseEmitter;
	}
	
	
	
	//Method to Dispatch events to connected Clients
	@PostMapping(value="dispatchEvent")
	public void dispatchEventsToAllClients(@RequestParam String freshNews) {
		for( SseEmitter emitter: emitters) {
			try {
				emitter.send(SseEmitter.event().name("latestNews").data(freshNews));
			} catch (IOException e) {
				e.printStackTrace();
				emitters.remove(emitter);
			}
			
			
		}
	}
	
	//Method to Dispatch events to connected Clients
		@PostMapping(value="notifyEvent")
		public void notifyEvent(@RequestParam String eventName,@RequestParam String eventValue) {
			for( SseEmitter emitter: emitters) {
				try {
					emitter.send(SseEmitter.event().name(eventName).data(eventValue));
				} catch (IOException e) {
					e.printStackTrace();
					emitters.remove(emitter);
				}
				
				
			}
		}
	
}
