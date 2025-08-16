package com.aitranscripter.ai.transcripter.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aitranscripter.ai.transcripter.Entity.messages;
import com.aitranscripter.ai.transcripter.Service.ChatService;
import com.aitranscripter.ai.transcripter.Service.UserService;

import java.util.ArrayList;
import java.util.Optional;

import javax.swing.Spring;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/user")
public class usercontroller {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;

    @PostMapping("/sendtranscriptertext")
    public ResponseEntity<?> sends(@RequestParam String id,@RequestBody String prompt,@RequestParam String model) {
        //TODO: process POST request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            String response = chatService.sendprompt(id, prompt,model, username);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            // TODO: handle exception
        }
        
       
    }
    @PostMapping("/sendtranscripterpdf")
    public ResponseEntity<?> sendpdf(@RequestParam String id,@RequestPart String prompt,@RequestPart MultipartFile pdf,@RequestParam String model) {
        //TODO: process POST request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            String response = chatService.sendpdft(id, pdf, prompt, model, username);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            // TODO: handle exception
        }
        
       
    }
    
    @PostMapping("/sendtranscripterdocx")
    public ResponseEntity<?> senddocx(@RequestParam String id,@RequestPart String prompt,@RequestPart MultipartFile pdf,@RequestParam String model) {
        //TODO: process POST request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            String response = chatService.sendpdft(id, pdf, prompt, model, username);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            // TODO: handle exception
        }
        
       
    }
    @GetMapping("/getmsg")
    public ResponseEntity<?> getmsg(@RequestParam String id) {
        try {
            Optional<messages> messages = chatService.getmsgg(id);
            return new ResponseEntity<>(messages,HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            // TODO: handle exception
        }
    }
    

    @GetMapping("/getallmsgs")
    public ResponseEntity<?> postMethodName() {
        //TODO: process POST request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            ArrayList<messages> messages = userService.getallconersation(username);
            return new ResponseEntity<>(messages,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            // TODO: handle exception
        }
        
    
    }
    


    

    
    
    
}
