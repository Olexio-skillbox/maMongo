package com.example.mongo.user.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

// db-10: PUT User (Edit by ID)
@Data
@NoArgsConstructor
public class EditUserRequest extends CreateUserRequest {
    private String id;
}
