package com.example.mongo.user.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserRequest {
    // private String firstName;   // Pre db-10
    // private String lastName;
    protected String firstName;   // db-10: Change (PUT)
    protected String lastName;

}
