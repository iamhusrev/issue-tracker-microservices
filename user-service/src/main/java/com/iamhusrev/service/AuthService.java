package com.iamhusrev.service;

import com.iamhusrev.dto.*;
import com.iamhusrev.entity.Organization;
import com.iamhusrev.entity.RefreshToken;
import com.iamhusrev.entity.Role;
import com.iamhusrev.entity.User;
import com.iamhusrev.exception.UserServiceException;
import com.iamhusrev.repository.OrganizationRepository;
import com.iamhusrev.repository.RefreshTokenRepository;
import com.iamhusrev.repository.RoleRepository;
import com.iamhusrev.repository.UserRepository;
import com.iamhusrev.security.JwtUtil;
import com.iamhusrev.util.MapperUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MapperUtil mapperUtil;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) throws UserServiceException {
        if (!request.getPassWord().equals(request.getConfirmPassword())) {
            throw new UserServiceException("Passwords do not match");
        }

        User existingUser = userRepository.findByUserName(request.getUserName());
        if (existingUser != null) {
            throw new UserServiceException("Username already exists");
        }

        Organization organization = organizationRepository.findByName(request.getOrganizationName())
                .orElseGet(() -> {
                    Organization newOrg = new Organization();
                    newOrg.setName(request.getOrganizationName());
                    newOrg.setDescription(request.getOrganizationName());
                    return organizationRepository.save(newOrg);
                });

        // Set organizationId on the org itself
        if (organization.getOrganizationId() == null) {
            organization.setOrganizationId(organization.getId());
            organizationRepository.save(organization);
        }

        Role defaultRole = roleRepository.findByDescription("Employee");
        if (defaultRole == null) {
            defaultRole = new Role("Employee");
            defaultRole = roleRepository.save(defaultRole);
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserName(request.getUserName());
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));
        user.setEnabled(true);
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setRole(defaultRole);
        user.setOrganizationId(organization.getId());
        user.setInsertDateTime(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return buildAuthResponse(savedUser);
    }

    public AuthResponseDTO login(LoginRequestDTO request) throws UserServiceException {
        User user = userRepository.findByUserName(request.getUserName());
        if (user == null) {
            throw new UserServiceException("Invalid username or password");
        }

        if (!passwordEncoder.matches(request.getPassWord(), user.getPassWord())) {
            throw new UserServiceException("Invalid username or password");
        }

        if (!user.getEnabled()) {
            throw new UserServiceException("User account is disabled");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) throws UserServiceException {
        RefreshToken storedToken = refreshTokenRepository
                .findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new UserServiceException("Invalid refresh token"));

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new UserServiceException("Refresh token has expired");
        }

        // Validate the JWT itself
        Claims claims;
        try {
            claims = jwtUtil.validateToken(request.getRefreshToken());
        } catch (Exception e) {
            throw new UserServiceException("Invalid refresh token");
        }

        // Revoke old token
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        Long userId = jwtUtil.getUserId(claims);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserServiceException("User not found"));

        return buildAuthResponse(user);
    }

    @Transactional
    public void logout(RefreshTokenRequestDTO request) {
        refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        String roleName = user.getRole() != null ? user.getRole().getDescription() : "Employee";

        String accessToken = jwtUtil.generateAccessToken(
                user.getId(), user.getUserName(), user.getOrganizationId(), roleName);

        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUserName());

        // Store refresh token in DB
        RefreshToken storedRefreshToken = new RefreshToken();
        storedRefreshToken.setToken(refreshToken);
        storedRefreshToken.setUserId(user.getId());
        storedRefreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(
                jwtUtil.getRefreshTokenExpirationMs() / 1000));
        storedRefreshToken.setRevoked(false);
        refreshTokenRepository.save(storedRefreshToken);

        UserDTO userDTO = mapperUtil.convert(user, new UserDTO());

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getAccessTokenExpirationMs() / 1000)
                .user(userDTO)
                .build();
    }
}
