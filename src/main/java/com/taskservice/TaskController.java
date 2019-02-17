package com.taskservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService service;

    private TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> createTask() {
        return new ResponseEntity<>(service.createTask(), ACCEPTED);
    }

    @GetMapping
    public Iterable<Task> getTasks() {
        return service.getAllTasks();
    }

    @RequestMapping(path = "/{id}", method = GET)
    public ResponseEntity<Task> getTasks(@PathVariable("id") String id) {
        if (!id.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (service.taskExists(id)) {
            return new ResponseEntity<>(service.getAllTasks(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
