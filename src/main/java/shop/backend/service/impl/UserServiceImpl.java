package shop.backend.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.BusinessException;
import shop.backend.comman.DuplicateResourceException;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.UserRequest;
import shop.backend.dto.response.UserResponse;
import shop.backend.entity.Role;
import shop.backend.entity.User;
import shop.backend.mapper.UserMapper;
import shop.backend.repository.RoleRepository;
import shop.backend.repository.UserRepository;
import shop.backend.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException("Password is required when creating a user");
        }

        Role role = findRole(request.getRoleId());

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = findEntity(id);

        if (!user.getUsername().equalsIgnoreCase(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setRole(findRole(request.getRoleId()));
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        // Password change is optional on update — blank/null means "keep existing"
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll(Boolean isActive) {
        return userRepository.findAllByOptionalActive(isActive).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long id) {
        User user = findEntity(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void activate(Long id) {
        User user = findEntity(id);
        user.setIsActive(true);
        userRepository.save(user);
    }

    private User findEntity(Long id) {
        return userRepository.findById(id).orElseThrow(() ->  ResourceNotFoundException.of("User", id));
    }

    private Role findRole(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> ResourceNotFoundException.of("Role", roleId));
    }
}