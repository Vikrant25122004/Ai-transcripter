package com.aitranscripter.ai.transcripter.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import jakarta.annotation.sql.DataSourceDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class messages {
    @Id
    private ObjectId id;
    private String model;
    private double temperature = 1;
    private int max_completion_tokens=1024;
    private double top_p=1;
    private boolean stream= false;
    private Object stop = null;   // keep flexible (null or array)
    private ArrayList<payload> messages;
    
}
