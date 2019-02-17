package com.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.taskservice.TaskStatus.FINISHED;
import static com.taskservice.TaskStatus.RUNNING;
import static java.time.LocalDateTime.now;
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
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formatDateTime = localDateTime.format(formatter);
        Task task = new Task(UUID.randomUUID().toString(), formatDateTime, TaskStatus.CREATED);
        repository.save(task);
        createdGuids.add(task.getGuid());
        return task.getGuid();
    }

    Iterable<Task> getAllTasks() {
        return repository.findAll();
    }

    Task getAllTasks(String id) {
        return repository.findById(id).orElseThrow(NullPointerException::new);
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
                log.debug("Task performed on: " + LocalDateTime.now() + "n" + "Thread's name: " + Thread.currentThread().getName());
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

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formatDateTime = localDateTime.format(formatter);
        task.setTimestamp(formatDateTime);
        task.setStatus(status);
        return task;
    }
}
