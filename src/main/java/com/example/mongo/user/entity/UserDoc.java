package com.example.mongo.user.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDoc {
    private ObjectId id;
    private String firstName;
    private String lastName;
}
