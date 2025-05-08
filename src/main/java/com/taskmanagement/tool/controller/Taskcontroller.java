package com.taskmanagement.tool.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.taskmanagement.tool.dto.Taskdetailsdto;
import com.taskmanagement.tool.kafka.KafkaProducer;
import com.taskmanagement.tool.model.Task;


import com.taskmanagement.tool.service.Taskservice;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/auth")
public class Taskcontroller {


    @Autowired
    private ObjectMapper objectMapper;
	@Autowired
    private Taskservice taskService;
	@Autowired
	private KafkaProducer kafkaproducer;
	
	private static final Logger logger = LoggerFactory.getLogger(Taskcontroller.class);
	
	@PostMapping("/createtask")
	@ResponseBody
	public ResponseEntity<ObjectNode> createtask(@RequestBody Taskdetailsdto taskdetailsdto, HttpServletRequest request) {
	    logger.info("Just entered createtask() method");
	    try {
	        Object userIdObj = request.getSession().getAttribute("userid");
	        if (userIdObj == null) {
	            logger.warn("User ID not found in session.");
	            ObjectNode errorNode = objectMapper.createObjectNode();
	            errorNode.put("error", "User ID is not available in session.");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorNode);
	        }

	        Task task = new Task();
	        task.setUserId(userIdObj.toString());
	        task.setTaskName(taskdetailsdto.getaskname());
	        task.settaskdescription(taskdetailsdto.gettaskdescription());
	        task.setStatus(taskdetailsdto.gettaskstatus());
	        task.settaskduedate(LocalDateTime.now()); 

	        taskService.saveTask(task);

	        ObjectNode taskNode = objectMapper.createObjectNode();
	        taskNode.put("taskId", task.getTaskId().toString());
	        taskNode.put("taskName", task.getTaskName());
	        taskNode.put("taskDescription", task.gettaskdescription());
	        taskNode.put("status", task.getStatus());
	        taskNode.put("dueDate", task.gettaskduedate().toString());

	        ObjectNode responseNode = objectMapper.createObjectNode();
	        responseNode.put("message", "Task successfully created.");
	        responseNode.set("task", taskNode);

	        logger.info("Just leaving createtask() method from Taskcontroller");
	        return ResponseEntity.status(HttpStatus.CREATED).body(responseNode);
	    } catch (Exception e) {
	        logger.debug("Exception in createtask() method: " + e.getMessage(), e);

	        ObjectNode errorResponse = objectMapper.createObjectNode();
	        errorResponse.put("error", "Task creation failed");
	        errorResponse.put("details", e.getMessage());

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	
	@GetMapping("/tasks/{taskid}")
    @ResponseBody
    public ResponseEntity<?> getTaskById(@PathVariable UUID taskid) {
		try {
			
		logger.info("Just entered getTaskById() method for taskid:{}",taskid);
        Optional<Task> taskOptional = taskService.gettaskbyid(taskid);
        if (taskOptional.isPresent()) {
            return ResponseEntity.ok(taskOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found with ID: " + taskid);
        }
        
		}catch (Exception e) {
	    	logger.debug("Exception in getTaskById() method" + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Task fetching failed: " + e.getMessage());
	    }
		
    }
	
	
	
	@DeleteMapping("/tasks/{taskid}")
	public ResponseEntity<String> deleteTask(@PathVariable UUID taskid) {
	    boolean isDeleted = taskService.deleteTaskById(taskid);
	    
	    if (isDeleted) {
	        return ResponseEntity.ok("Task successfully deleted");
	      
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body("Task not found with ID to delete: " + taskid);
	    }
	}
	
	
	
	@PatchMapping("/tasks/{taskid}")
	@ResponseBody
	public ResponseEntity<String> updateTaskById(@RequestBody Taskdetailsdto taskdetailsdto, @PathVariable UUID taskid) {
	    logger.info("Just entered updateTaskById() method for task: {}", taskid);

	    try {
	        Optional<Task> optionalTask = taskService.gettaskbyid(taskid);

	        if (optionalTask.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found with ID: " + taskid);
	        }

	        Task existingTask = optionalTask.get();

	        
	        if (taskdetailsdto.getaskname() != null) {
	            existingTask.setTaskName(taskdetailsdto.getaskname());
	        }

	        if (taskdetailsdto.gettaskdescription() != null) {
	            existingTask.settaskdescription(taskdetailsdto.gettaskdescription());
	        }

	        if (taskdetailsdto.gettaskstatus() != null) {
	            existingTask.setStatus(taskdetailsdto.gettaskstatus());
	        }

	        if (taskdetailsdto.gettaskdueDate() != null) {
	            existingTask.settaskduedate(taskdetailsdto.gettaskdueDate());
	        }

	        existingTask.setDateModified(LocalDateTime.now());

	        taskService.updateTask(existingTask);

	        logger.info("Just leaving updateTaskById() method for task: {}", taskid);
	        return ResponseEntity.ok("Task successfully updated");

	    } catch (Exception e) {
	        logger.debug("Exception in updateTaskById() method: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Error updating task: " + e.getMessage());
	    }
	}



	
	
	@GetMapping("/tasks/user/{userId}")
	@ResponseBody
	public ResponseEntity<List<Task>> getTasksByUsername(@PathVariable String userId) {
	    List<Task> taskList = taskService.gettaskbyuserId(userId);

	    if (taskList == null || taskList.isEmpty()) {
	        return ResponseEntity.noContent().build(); 
	    }

	    return ResponseEntity.ok(taskList); 
	}


	@PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
		kafkaproducer.sendMessage("my-topic", message);
        return "Message sent!";
    }
	


}
