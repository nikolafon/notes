package com.example.notes.controller;

import com.example.notes.resource.User;
import com.example.notes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * Find users.
     *
     * @param query - query expression to filter by
     * @param pageable - page request
     * @return list of elements in requested page
     */
    @GetMapping
    private PagedModel<User> find(@RequestParam(required = false) String query, Pageable pageable) {
        return new PagedModel<>(userService.find(query, pageable));
    }
    /**
     * Get user by id.
     *
     * @param id - user id
     * @return user
     */
    @GetMapping(path = "/{id}")
    private User get(@PathVariable String id) {
        return userService.get(id);
    }
    /**
     * Create user.
     *
     * @param user - user
     * @return user
     */
    @PostMapping
    private User create(@RequestBody User user) {
        return userService.create(user);
    }
    /**
     * Update user.
     *
     * @param user - user
     * @param id - user id
     * @return user
     */
    @PutMapping(path = "/{id}")
    private User update(@RequestBody User user, @PathVariable String id) {
        user.setId(id);
        return userService.update(user);
    }
    /**
     * Delete user.
     *
     * @param id - user id
     */
    @RequestMapping(path = "/{id}")
    private void delete(@PathVariable String id) {
        userService.delete(id);
    }

}
