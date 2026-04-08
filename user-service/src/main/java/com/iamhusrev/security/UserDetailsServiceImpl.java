package com.iamhusrev.security;

import com.iamhusrev.entity.User;
import com.iamhusrev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        String role = user.getRole() != null ? user.getRole().getDescription() : "USER";
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassWord())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())))
                .disabled(!Boolean.TRUE.equals(user.getEnabled()))
                .build();
    }
}
