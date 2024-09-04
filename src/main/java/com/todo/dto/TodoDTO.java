package com.todo.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TodoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String fileURL;
}
