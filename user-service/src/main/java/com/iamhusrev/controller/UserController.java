package com.iamhusrev.controller;

import com.iamhusrev.dto.UserDTO;
import com.iamhusrev.entity.ResponseWrapper;
import com.iamhusrev.exception.UserServiceException;
import com.iamhusrev.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserFallbackHandler fallbackHandler;


    @GetMapping
    @CircuitBreaker(name = "user-service", fallbackMethod = "getUsersFallback")
    public ResponseEntity<ResponseWrapper> getUsers() {
        List<UserDTO> userDTOList = userService.listAllUsers();
        return ResponseEntity.ok(new ResponseWrapper("Users are successfully retrieved", userDTOList, HttpStatus.OK));
    }

    @GetMapping("/{userName}")
    @CircuitBreaker(name = "user-service", fallbackMethod = "getUserByUserNameFallback")
    public ResponseEntity<ResponseWrapper> getUserByUserName(@PathVariable String userName) throws AccessDeniedException {
        UserDTO user = userService.findByUserName(userName);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully retrieved", user, HttpStatus.OK));
    }

    @PostMapping
    @CircuitBreaker(name = "user-service", fallbackMethod = "createUpdateFallback")
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO user) throws UserServiceException {
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("User is successfully created", HttpStatus.CREATED));
    }

    @PutMapping
    @CircuitBreaker(name = "user-service", fallbackMethod = "createUpdateFallback")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO user) throws UserServiceException, AccessDeniedException {
        userService.update(user);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully updated", user, HttpStatus.OK));
    }

    @DeleteMapping("/{userName}")
    @CircuitBreaker(name = "user-service", fallbackMethod = "deleteUserFallback")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable String userName) {
        userService.deleteByUserName(userName);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully deleted", HttpStatus.OK));
    }

    public ResponseEntity<ResponseWrapper> getUsersFallback(Throwable t) {
        return fallbackHandler.handleListFallback(t);
    }

    public ResponseEntity<ResponseWrapper> getUserByUserNameFallback(String userName, Throwable t) {
        return fallbackHandler.handleSingleUserFallback(userName, t);
    }

    public ResponseEntity<ResponseWrapper> createUpdateFallback(UserDTO user, Throwable t) {
        return fallbackHandler.handleUserModificationFallback(user, t);
    }

    public ResponseEntity<ResponseWrapper> deleteUserFallback(String userName, Throwable t) {
        return fallbackHandler.handleUserDeletionFallback(userName, t);
    }
}