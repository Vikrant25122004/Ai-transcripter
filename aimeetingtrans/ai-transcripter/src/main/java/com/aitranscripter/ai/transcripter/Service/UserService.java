package com.aitranscripter.ai.transcripter.Service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aitranscripter.ai.transcripter.Entity.messages;
import com.aitranscripter.ai.transcripter.Entity.user;
import com.aitranscripter.ai.transcripter.Repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Boolean register(user user){
        if (user.getEmail()!=null && user.getPassword()!=null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }
    public user getuser(String email){
        user user = userRepository.findByemail(email);
        return user;
    }

    public ArrayList<messages> getallconersation(String email){
        user user = userRepository.findByemail(email);
        return user.getMessages();
    }
}
