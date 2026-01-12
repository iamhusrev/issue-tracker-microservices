package com.iamhusrev.service;

import com.iamhusrev.dto.UserDTO;
import com.iamhusrev.entity.ResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", path = "/api/v1/user", fallback = UserClientFallback.class)
public interface UserClientService {

    @GetMapping
    ResponseWrapper getUsers();

    @GetMapping("/{userName}")
    ResponseWrapper getUserByUserName(@PathVariable String userName);

    @PostMapping
    ResponseWrapper createUser(@RequestBody UserDTO user);

    @PutMapping
    ResponseWrapper updateUser(@RequestBody UserDTO user);

    @DeleteMapping("/{userName}")
    ResponseWrapper deleteUser(@PathVariable String userName);
}