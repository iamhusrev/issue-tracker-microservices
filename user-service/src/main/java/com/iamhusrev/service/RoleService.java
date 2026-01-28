package com.iamhusrev.service;

import com.iamhusrev.dto.RoleDTO;
import com.iamhusrev.entity.Role;
import com.iamhusrev.exception.UserServiceException;
import com.iamhusrev.repository.RoleRepository;
import com.iamhusrev.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {


    private final RoleRepository roleRepository;
    private final MapperUtil mapperUtil;


    public List<RoleDTO> listAllRoles() {
        List<Role> list = roleRepository.findAll();
        return list.stream().map(obj -> mapperUtil.convert(obj, new RoleDTO())).collect(Collectors.toList());
    }

    public RoleDTO findById(Long id) throws UserServiceException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new UserServiceException("Role does not exists"));
        return mapperUtil.convert(role, new RoleDTO());
    }
}
