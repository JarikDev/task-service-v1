package com.taskservice.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static com.taskservice.task.TaskStatus.FINISHED;
import static com.taskservice.task.TaskStatus.RUNNING;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.Optional.ofNullable;

@Service
public class TaskService {
    Logger log = LoggerFactory.getLogger(TaskService.class);
    private TaskRepository repository;
    private final Deque<String> createdGuids;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
        createdGuids = new ArrayDeque<>();
    }

    String createTask() {
        String guid = UUID.randomUUID().toString();
        Task task = new Task(guid, now().format(ISO_LOCAL_DATE_TIME), TaskStatus.CREATED);
        repository.save(task);
        createdGuids.add(guid);
        return guid;
    }

    Iterable<Task> getAllTasks() {
        return repository.findAll();
    }

    Task getTask(String id) {
        return repository.findById(id).orElseThrow(() -> {
            log.error("Task with id " + id + " does not exist.");
            return new NullPointerException("Task with id " + id + " does not exist.");
        });
    }

    public boolean taskExists(String id) {
        return repository.findById(id).isPresent();
    }

    public void updateTaskStatus() {
        String guid = ofNullable(createdGuids.pollFirst()).orElseThrow(() -> {
            log.error("Created task queue is empty");
            return new NullPointerException("Created task queue is empty");
        });
        Task task = updateTask(guid, RUNNING);
        repository.save(task);
        TimerTask timerTask = new TimerTask() {
            public void run() {
                Task task = updateTask(guid, FINISHED);
                repository.save(task);
                log.debug("Task performed on: " + now() + "n" + "Thread's name: " + Thread.currentThread().getName());
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 120000L);
    }

    private Task updateTask(String guid, TaskStatus status) {
        Task task = repository.findById(guid).orElseThrow(() -> {
            log.error("Task with GUID=" + guid + "was not found in repository");
            return new NullPointerException("Task with GUID=" + guid + "was not found in repository");
        });
        task.setTimestamp(now().format(ISO_LOCAL_DATE_TIME));
        task.setStatus(status);
        return task;
    }
}
