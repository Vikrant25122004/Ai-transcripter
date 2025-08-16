package com.aitranscripter.ai.transcripter.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitranscripter.ai.transcripter.Entity.user;
import com.aitranscripter.ai.transcripter.Service.UserService;
import com.aitranscripter.ai.transcripter.Service.appauth;
import com.aitranscripter.ai.transcripter.utils.jwtutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/public")
public class publiccontroller {


    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private jwtutils jwtutils;
    @Autowired
    private appauth appauth;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody user user) {
        //TODO: process POST request
        try {
            Boolean reg = userService.register(user);
            if(reg==true){
                return new ResponseEntity<>(HttpStatus.OK);

            }
            else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
     
     @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody user user) {
        try{
           authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            UserDetails userDetails = appauth.loadUserByUsername(user.getEmail());
            String jwt = jwtutils.generateToken(userDetails.getUsername());
            System.out.println("login for "  + user.getEmail());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect username or password");
            // Add logging here!
        }

    }
    
    
}
