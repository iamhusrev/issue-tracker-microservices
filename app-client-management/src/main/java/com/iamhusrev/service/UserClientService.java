package com.iamhusrev.service;

import com.iamhusrev.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserClientService {

    @GetMapping("/user/{username}")
    UserResponseDTO getUserDTOByUserName(@PathVariable String username);
}
