package com.example.notes.init;

import com.example.notes.resource.User;
import com.example.notes.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class InitDb {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        User exampleUser = new User();
        exampleUser.setUsername("admin");
        Optional<User> optionalUser = userRepository.findOne(Example.of(exampleUser));
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setFirstName("Nikola");
            user.setLastName("Nikolic");
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("nikola.nikolic@outlook.com");
            user.setAuthorities(Set.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("user")));
            userRepository.save(user);
        }
    }
}
