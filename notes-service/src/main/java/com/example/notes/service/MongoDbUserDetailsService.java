package com.example.notes.service;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MongoDbUserDetailsService implements UserDetailsService {

    @Autowired
    private ResourceRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.find(new BasicQuery(String.format("{ username: '%s' }", username)), User.class).stream().findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
