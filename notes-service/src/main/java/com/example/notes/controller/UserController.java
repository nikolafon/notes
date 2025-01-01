package com.example.notes.controller;

import com.example.notes.resource.User;
import com.example.notes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    private Page<User> find(@RequestParam(required = false) String query, Pageable pageable) {
        return userService.find(query, pageable);
    }

    @GetMapping(path = "/{id}")
    private User get(@PathVariable String id) {
        return userService.get(id);
    }

    @PostMapping
    private User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping(path = "/{id}")
    private User update(@RequestBody User user, @PathVariable String id) {
        user.setId(id);
        return userService.update(user);
    }

    @DeleteMapping(path = "/{id}")
    private void delete(@PathVariable String id) {
        userService.delete(id);
    }

}
