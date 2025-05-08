package com.taskmanagement.tool.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Taskdetailsdto {
//	private UUID taskid;
    private String taskname;
    private String taskdescription;
    private LocalDateTime taskdueDate;
    private String taskstatus;

    
    public Taskdetailsdto() {}

    public Taskdetailsdto(String taskname, String taskdescription, LocalDateTime taskdueDate, String taskstatus) {
    	
        this.taskname = taskname;
        this.taskdescription = taskdescription;
        this.taskdueDate = taskdueDate;
        this.taskstatus = taskstatus;
    }

    


	
	
    public String getaskname() {
        return taskname;
    }

    public void settaskname(String taskname) {
        this.taskname = taskname;
    }

    public String gettaskdescription() {
        return taskdescription;
    }

    public void settaskdescription(String taskdescription) {
        this.taskdescription = taskdescription;
    }

    public LocalDateTime gettaskdueDate() {
        return taskdueDate	;
    }

    public void settaskdueDate(LocalDateTime taskdueDate) {
        this.taskdueDate = taskdueDate;
    }

    public String gettaskstatus() {
        return taskstatus;
    }

    public void setStatus(String taskstatus) {
        this.taskstatus = taskstatus;
    }
}
