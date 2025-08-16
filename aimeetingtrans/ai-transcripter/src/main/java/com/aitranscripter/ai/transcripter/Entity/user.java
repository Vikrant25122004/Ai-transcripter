package com.aitranscripter.ai.transcripter.Entity;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class user {
    @Id
    private ObjectId id;
    private String email;
    private String password;
    private ArrayList<messages> messages;
}
