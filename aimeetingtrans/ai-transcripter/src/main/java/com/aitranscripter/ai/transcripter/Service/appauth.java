package com.aitranscripter.ai.transcripter.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import com.aitranscripter.ai.transcripter.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import com.aitranscripter.ai.transcripter.Entity.user;

@Service
public class appauth implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Username is empty");
        }
        if (username.contains("@")) {
            user user = userRepository.findByemail(username);
            if (user!=null) {
                 if (user.getPassword() == null) {
                    throw new UsernameNotFoundException("Doctor's password not set");
                }
                else{
                    return User.builder().username(user.getEmail()).password(user.getPassword()).build();

                }
                
            }

            
        }
       throw new UsernameNotFoundException("User not found with identifier: " + username);

    }
    
}
