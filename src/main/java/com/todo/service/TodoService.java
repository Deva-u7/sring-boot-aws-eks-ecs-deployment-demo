package com.todo.service;

import com.todo.dto.TodoDTO;
import com.todo.entity.Todo;
import com.todo.mapper.ModelMapper;
import com.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AWSS3FileUploadService awss3FileUploadService;

    public TodoDTO addTodo(TodoDTO todoDTO, MultipartFile file) {
        try {
            Todo todo = modelMapper.toEntity(todoDTO);
            todoRepository.save(todo);
            if (file != null){
                awss3FileUploadService.fileUpload(file);
            }
            return modelMapper.toDTO(todo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Todo> getTodo() {
        List<Todo> todoList = todoRepository.findAll();
        return todoList;
    }

    public Todo getTodoById(Long todoId) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(null);
        return todo;
    }
}
