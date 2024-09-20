package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class Controller {

    @Autowired
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public EntityModel<User> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(new User("user not found"));
        log.debug("Found user: {}", user);

        return EntityModel.of(user,
                linkTo(methodOn(Controller.class).getUser(id)).withSelfRel(),
                linkTo(methodOn(Controller.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(Controller.class).createUser("user name")).withRel("newUser"),
                linkTo(methodOn(Controller.class).updateUser(id, "new user name")).withRel("updatedUser"),
                linkTo(methodOn(Controller.class).deleteUser(id)).withRel("deleteUser"));
    }

    @GetMapping("")
    public CollectionModel<EntityModel<User>> getAllUsers() {
        List<EntityModel<User>> users = userRepository.findAll().stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(Controller.class).getUser(user.getId())).withSelfRel()))
                .collect(Collectors.toList());
        log.debug("GET users: {}", users);

        return CollectionModel.of(users,
                linkTo(methodOn(Controller.class).getAllUsers()).withSelfRel(),
                linkTo(methodOn(Controller.class).getUser(0L)).withRel("getUser"),
                linkTo(methodOn(Controller.class).createUser("user name")).withRel("newUser"),
                linkTo(methodOn(Controller.class).updateUser(0L, "new user name")).withRel("updatedUser"),
                linkTo(methodOn(Controller.class).deleteUser(0L)).withRel("deleteUser"));
    }

    @PostMapping("/user/{text}")
    public EntityModel<User> createUser(@PathVariable String text) {
        User user = userRepository.save(new User(text));
        log.debug("Create user: {}", user);

        return EntityModel.of(user,
                linkTo(methodOn(Controller.class).createUser(text)).withSelfRel(),
                linkTo(methodOn(Controller.class).getUser(0L)).withRel("getUser"),
                linkTo(methodOn(Controller.class).getAllUsers()).withRel("getUsers"),
                linkTo(methodOn(Controller.class).updateUser(0L, "new user name")).withRel("updatedUser"),
                linkTo(methodOn(Controller.class).deleteUser(0L)).withRel("deleteUser"));
    }

    @PutMapping("/user/{id}")
    public EntityModel<User> updateUser(@PathVariable Long id, @RequestParam(value = "name", required = true) String name) {
        User user = userRepository.findById(id).get();
        user.setName(name);

        userRepository.save(user);
        log.debug("Update user: {}", user);

        return EntityModel.of(user,
                linkTo(methodOn(Controller.class).updateUser(id, name)).withSelfRel(),
                linkTo(methodOn(Controller.class).getUser(0L)).withRel("getUser"),
                linkTo(methodOn(Controller.class).getAllUsers()).withRel("getUsers"),
                linkTo(methodOn(Controller.class).createUser("user name")).withRel("newUser"),
                linkTo(methodOn(Controller.class).deleteUser(0L)).withRel("deleteUser"));
    }

    @DeleteMapping("/user/{id}")
    public EntityModel<Long> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
        log.debug("Delete user: {}", id);

        return EntityModel.of(id,
                linkTo(methodOn(Controller.class).deleteUser(id)).withSelfRel(),
                linkTo(methodOn(Controller.class).getUser(0L)).withRel("getUser"),
                linkTo(methodOn(Controller.class).getAllUsers()).withRel("getUsers"),
                linkTo(methodOn(Controller.class).createUser("user name")).withRel("newUser"),
                linkTo(methodOn(Controller.class).updateUser(id, "new user name")).withRel("updatedUser"));
    }
}
