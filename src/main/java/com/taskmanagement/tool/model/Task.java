package com.taskmanagement.tool.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "task")
public class Task {
	
	
	
	@Id
	@GeneratedValue
    @Column(name = "task_id",nullable = true, unique = true)
    private UUID taskId;
    
    @Column(name = "task_name",nullable = true, unique = true)
    private String taskName;
    
    
    @Column(name = "taskdescription",nullable = true, unique = true)
    private String taskdescription;
    
    @Column(name = "status",nullable = true, unique = false)
    private String status;
    
    @Column(name = "taskduedate",nullable = true, unique = false)
    private LocalDateTime taskduedate;
    
    @Column(name = "date_modified",nullable = true, unique = true)
    private LocalDateTime dateModified;
    
    
    @Column(name = "user_id",nullable = true, unique = false)
    private String userId;


	public UUID getTaskId() {
		return taskId;
	}


	public void setTaskId(UUID taskId) {
		this.taskId = taskId;
	}


	public String getTaskName() {
		return taskName;
	}


	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}


	public String gettaskdescription() {
		return taskdescription;
	}


	public void settaskdescription(String taskdescription) {
		this.taskdescription = taskdescription;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public LocalDateTime gettaskduedate() {
		return taskduedate;
	}


	public void settaskduedate(LocalDateTime localDate) {
		this.taskduedate = localDate;
	}


	public LocalDateTime getDateModified() {
		return dateModified;
	}


	public void setDateModified(LocalDateTime dateModified) {
		this.dateModified = dateModified;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}





	
    


} 

 
    




