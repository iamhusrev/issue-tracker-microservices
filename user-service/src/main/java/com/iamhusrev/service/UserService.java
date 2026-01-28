package com.iamhusrev.service;

import com.iamhusrev.dto.UserDTO;
import com.iamhusrev.entity.User;
import com.iamhusrev.exception.UserServiceException;
import com.iamhusrev.repository.UserRepository;
import com.iamhusrev.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;


    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        return list.stream().map(obj -> mapperUtil.convert(obj, new UserDTO())).collect(Collectors.toList());
    }

    public UserDTO findByUserName(String username) throws AccessDeniedException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new AccessDeniedException("User Not Found");
        }
        return mapperUtil.convert(user, new UserDTO());
    }

    public UserDTO save(UserDTO dto) throws UserServiceException {

        User foundUser = userRepository.findByUserName(dto.getUserName());

        if (foundUser != null) {
            throw new UserServiceException("User already exists");
        }

        User user = mapperUtil.convert(dto, new User());

        User save = userRepository.save(user);

        return mapperUtil.convert(save, new UserDTO());

    }

    public UserDTO update(UserDTO dto) throws UserServiceException, AccessDeniedException {

        User user = userRepository.findByUserName(dto.getUserName());

        if (user == null) {
            throw new UserServiceException("User Does Not Exists");
        }
        User convertedUser = mapperUtil.convert(dto, new User());

        convertedUser.setEnabled(true);

        convertedUser.setId(user.getId());
        //save updated user
        userRepository.save(convertedUser);

        return findByUserName(dto.getUserName());
    }

    public void delete(String username) throws UserServiceException {
        User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new UserServiceException("User Does Not Exists");
        }

        user.setUserName(user.getUserName() + "-" + user.getId());

        user.setIsDeleted(true);
        userRepository.save(user);
    }

    public void deleteByUserName(String username) {
        userRepository.deleteByUserName(username);
    }



}
