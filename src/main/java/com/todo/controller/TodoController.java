package com.todo.controller;

import com.todo.dto.TodoDTO;
import com.todo.entity.Todo;
import com.todo.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TodoController {

    private TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/todo")
    public ResponseEntity<TodoDTO> addTodo(@RequestBody TodoDTO todoDTO, @RequestParam(value = "file", required = false) MultipartFile file) {
        log.info("Adding New Record:");
        TodoDTO response = todoService.addTodo(todoDTO, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/todo")
    public ResponseEntity<List<Todo>> getTodo() {
        List<Todo> todo = todoService.getTodo();
        return ResponseEntity.ok(todo);
    }

    @GetMapping("/todo/{todo-id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable("todo-id") Long todoId) {
        Todo todo = todoService.getTodoById(todoId);
        return ResponseEntity.ok(todo);
    }
}
