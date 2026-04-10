package com.iamhusrev.service;

import com.iamhusrev.dto.UserDTO;
import com.iamhusrev.entity.User;
import com.iamhusrev.event.EventPublisher;
import com.iamhusrev.event.UserEvent;
import com.iamhusrev.exception.UserServiceException;
import com.iamhusrev.repository.UserRepository;
import com.iamhusrev.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final EventPublisher eventPublisher;


    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        return list.stream().map(obj -> mapperUtil.convert(obj, new UserDTO())).collect(Collectors.toList());
    }

    public UserDTO findByUserName(String username) throws AccessDeniedException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            return null;
        }
        return mapperUtil.convert(user, new UserDTO());
    }

    @Transactional
    public UserDTO save(UserDTO dto) throws UserServiceException {

        User foundUser = userRepository.findByUserName(dto.getUserName());

        if (foundUser != null) {
            throw new UserServiceException("User already exists");
        }

        User user = mapperUtil.convert(dto, new User());

        User save = userRepository.save(user);

        eventPublisher.publish(new UserEvent("user.created", save.getId(), save.getUserName(),
                save.getFirstName(), save.getLastName()));

        return mapperUtil.convert(save, new UserDTO());

    }

    @Transactional
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

        eventPublisher.publish(new UserEvent("user.updated", convertedUser.getId(), convertedUser.getUserName(),
                convertedUser.getFirstName(), convertedUser.getLastName()));

        return findByUserName(dto.getUserName());
    }

    @Transactional
    public void delete(String username) throws UserServiceException {
        User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new UserServiceException("User Does Not Exists");
        }

        user.setUserName(user.getUserName() + "-" + user.getId());

        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Transactional
    public void deleteByUserName(String username) {
        userRepository.deleteByUserName(username);

        eventPublisher.publish(new UserEvent("user.deleted", null, username, null, null));
    }



}
