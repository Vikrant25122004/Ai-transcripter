package com.aitranscripter.ai.transcripter.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class payload {

    private String role = "user";
    private String content;
    private String response;
    
}
