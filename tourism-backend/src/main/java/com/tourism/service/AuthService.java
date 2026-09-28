package com.tourism.service;

import com.tourism.dto.request.LoginRequest;
import com.tourism.dto.request.RegisterRequest;
import com.tourism.dto.response.AuthResponse;
import com.tourism.entity.CustomerProfile;
import com.tourism.entity.User;
import com.tourism.entity.enums.Role;
import com.tourism.entity.enums.UserStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.exception.ConflictException;
import com.tourism.repository.CustomerProfileRepository;
import com.tourism.repository.UserRepository;
import com.tourism.security.AppUserDetails;
import com.tourism.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("USERNAME_TAKEN", "This username is already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("EMAIL_TAKEN", "This email is already registered.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        // Public self-registration is always a CUSTOMER account, regardless of
        // what the client sends - never trust frontend roles.
        user.setRole(Role.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        customerProfileRepository.save(profile);

        auditService.log("USER_REGISTERED", "User", String.valueOf(user.getId()), "New customer registered: " + user.getUsername());

        String token = jwtService.generateToken(new AppUserDetails(user));
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name(), user.getFullName());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("This account is not active. Please contact support.");
        }

        String token = jwtService.generateToken(new AppUserDetails(user));
        auditService.log("USER_LOGIN", "User", String.valueOf(user.getId()), "Login: " + user.getUsername());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name(), user.getFullName());
    }
}
