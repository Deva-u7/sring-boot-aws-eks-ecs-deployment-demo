package com.todo.mapper;

import com.todo.dto.TodoDTO;
import com.todo.entity.Todo;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelMapper {
    TodoDTO toDTO(Todo todo);
    Todo toEntity(TodoDTO todoDTO);
}
