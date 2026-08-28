package shop.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.BusinessException;
import shop.backend.comman.DuplicateResourceException;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.RoleRequest;
import shop.backend.dto.response.RoleResponse;
import shop.backend.entity.Role;
import shop.backend.mapper.RoleMapper;
import shop.backend.repository.RoleRepository;
import shop.backend.repository.UserRepository;
import shop.backend.service.RoleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("A role named '" + request.getName() + "' already exists");
        }
        Role role = Role.builder()
                .name(request.getName().toUpperCase())
                .description(request.getDescription())
                .build();
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = findEntity(id);

        if (!role.getName().equalsIgnoreCase(request.getName())
                && roleRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("A role named '" + request.getName() + "' already exists");
        }

        role.setName(request.getName().toUpperCase());
        role.setDescription(request.getDescription());
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        return roleMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Role role = findEntity(id);
        if (!userRepository.findAllByOptionalActive(null).isEmpty()
                && userRepository.findAllByOptionalActive(null).stream()
                        .anyMatch(u -> u.getRole().getId().equals(id))) {
            throw new BusinessException("Cannot delete a role that is still assigned to users");
        }
        roleRepository.delete(role);
    }

    private Role findEntity(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Role", id));
    }
}