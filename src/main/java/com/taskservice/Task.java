package com.taskservice;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Task {
    @Id
    private String guid;
    private String timestamp;
    private TaskStatus status;

    public Task() { }

    public Task(String guid, String timestamp, TaskStatus status) {
        this.guid = guid;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

}
