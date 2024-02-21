package com.pockcomoc.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import com.pockcomoc.model.Task;

import reactor.core.publisher.Mono;


@Controller
public class TaskViewController {

	private final WebClient webClient;

	public TaskViewController(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
	}

	@GetMapping("/tasks")
	public String getAllTasks(Model model) {
		List<Task> tasks = webClient.get().uri("/tasks")	
				.retrieve()
				.bodyToFlux(Task.class)
				.collectList()
				.block();
		model.addAttribute("tasks", tasks);
		return "tasks";
	}

	
	@PostMapping("/tasks/add")
	public String addTask(@ModelAttribute Task task) {
	    webClient.post().uri("/tasks/add")
	        .body(Mono.just(task), Task.class)
	        .retrieve()
	        .toBodilessEntity()
	        .block();
	    return "redirect:/tasks";
	}

	@PostMapping("/tasks/delete")
	public String deleteTask(@RequestParam("taskId") String taskId) {
	    webClient.delete().uri("/tasks/delete/{id}", taskId)
	        .retrieve()
	        .toBodilessEntity()
	        .block();
	    return "redirect:/tasks";
	}
	
	@GetMapping("/tasks/status")
	public String getTasksByStatus(@RequestParam("status") String status, Model model) {
	    List<Task> tasks = webClient.get().uri("/tasks/status/{status}", status)
	        .retrieve()
	        .bodyToFlux(Task.class)
	        .collectList()
	        .block();
	    model.addAttribute("tasks", tasks);
	    return "tasks";
	}

	
	@PostMapping("/tasks/update/{id}")
	public String updateTaskStatus(@PathVariable("id") Long id, @ModelAttribute Task task) {
	    webClient.put().uri("/tasks/update/{id}", id)
	        .body(Mono.just(task), Task.class)
	        .retrieve()
	        .toBodilessEntity()
	        .block();
	    return "redirect:/tasks";
	}

}
