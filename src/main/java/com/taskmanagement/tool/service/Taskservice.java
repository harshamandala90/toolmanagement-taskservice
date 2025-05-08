package com.taskmanagement.tool.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Service;

import com.taskmanagement.tool.model.Task;

import com.taskmanagement.tool.repository.Jpataskrepository;


@Service
public class Taskservice {

	@Autowired
    private Jpataskrepository taskRepository;

	public void saveTask(Task task) {
		taskRepository.save(task);		
	}

	public List<Task> getAllUsers() {
        return taskRepository.findAll();
    }

	

	public void updateTask(Task task) {
		  taskRepository.save(task);
		}


	

	public Optional<Task> gettaskbyid(UUID taskid) {
		return taskRepository.findById(taskid);
	}

	public Optional<Task> gettaskbyuserid(UUID taskid) {
		return taskRepository.findById(taskid);
	}

	public boolean deleteTaskById(UUID taskid) {
	    if (taskRepository.existsById(taskid)) {
	        taskRepository.deleteById(taskid);
	        return true;
	    } else {
	        return false;
	    }
	    
	    
	}

	public List<Task> gettaskbyuserId(String userId) {
		return taskRepository.findByuserId(userId);
	}

	

	
	

	
	
	
	


	
}
