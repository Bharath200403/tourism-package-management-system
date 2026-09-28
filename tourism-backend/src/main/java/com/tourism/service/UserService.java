package com.tourism.service;

import com.tourism.dto.request.ProfileUpdateRequest;
import com.tourism.dto.request.RegisterRequest;
import com.tourism.dto.response.UserResponseDTO;
import com.tourism.entity.CustomerProfile;
import com.tourism.entity.OperatorProfile;
import com.tourism.entity.User;
import com.tourism.entity.enums.Role;
import com.tourism.entity.enums.UserStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.exception.ConflictException;
import com.tourism.exception.ResourceNotFoundException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.CustomerProfileRepository;
import com.tourism.repository.OperatorProfileRepository;
import com.tourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final OperatorProfileRepository operatorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public List<UserResponseDTO> listByRole(Role role) {
        return userRepository.findByRole(role).stream().map(DtoMapper::toUserDTO).toList();
    }

    public List<UserResponseDTO> listAll() {
        return userRepository.findAll().stream().map(DtoMapper::toUserDTO).toList();
    }

    public UserResponseDTO getById(Long id) {
        return DtoMapper.toUserDTO(getUserOrThrow(id));
    }

    /** ADMIN-only creation of TOUR_OPERATOR or ADMIN accounts. */
    @Transactional
    public UserResponseDTO createStaffUser(RegisterRequest request, Role role) {
        if (role == Role.CUSTOMER) {
            throw new BadRequestException("Use the public registration endpoint for customer accounts.");
        }
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
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        if (role == Role.TOUR_OPERATOR) {
            OperatorProfile profile = new OperatorProfile();
            profile.setUser(user);
            operatorProfileRepository.save(profile);
        }

        auditService.log("USER_CREATED", "User", String.valueOf(user.getId()),
                "Created " + role + " account: " + user.getUsername());
        return DtoMapper.toUserDTO(user);
    }

    @Transactional
    public UserResponseDTO setStatus(Long userId, UserStatus status) {
        User user = getUserOrThrow(userId);
        user.setStatus(status);
        userRepository.save(user);
        auditService.log("USER_STATUS_CHANGED", "User", String.valueOf(userId), "New status: " + status);
        return DtoMapper.toUserDTO(user);
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getUserOrThrow(userId);
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        userRepository.save(user);

        if (user.getRole() == Role.CUSTOMER) {
            CustomerProfile profile = customerProfileRepository.findByUserId(userId).orElseGet(() -> {
                CustomerProfile p = new CustomerProfile();
                p.setUser(user);
                return p;
            });
            if (request.getAddress() != null) profile.setAddress(request.getAddress());
            if (request.getCity() != null) profile.setCity(request.getCity());
            if (request.getState() != null) profile.setState(request.getState());
            if (request.getCountry() != null) profile.setCountry(request.getCountry());
            if (request.getPreferredTravelType() != null) profile.setPreferredTravelType(request.getPreferredTravelType());
            if (request.getPreferredBudget() != null) profile.setPreferredBudget(request.getPreferredBudget().doubleValue());
            customerProfileRepository.save(profile);
        }
    }

    User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
