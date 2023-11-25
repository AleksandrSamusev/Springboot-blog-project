package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.JWTAuthResponse;
import dev.practice.mainApp.dtos.user.LoginDto;
import dev.practice.mainApp.dtos.user.UserNewDto;
import dev.practice.mainApp.exceptions.TodoAPIException;
import dev.practice.mainApp.mappers.UserMapper;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.security.JWTTokenProvider;
import dev.practice.mainApp.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    @Override
    public String register(UserNewDto userNewDto) {

        // check if username is already exists in DB
        if (userRepository.existsByUsername(userNewDto.getUsername())) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        // check if email is already exists in DB
        if (userRepository.existsByEmail(userNewDto.getEmail())) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = UserMapper.toUser(userNewDto);
        user.setPassword(passwordEncoder.encode(userNewDto.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER");
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public JWTAuthResponse login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        Optional<User> userOptional = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail());

        String role = null;

        if (userOptional.isPresent()) {
            User loggedInUser = userOptional.get();
            Optional<Role> optionalRole = loggedInUser.getRoles().stream().findFirst();
            if (optionalRole.isPresent()) {
                Role userRole = optionalRole.get();
                role = userRole.getName();
            }
        }

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setRole(role);
        jwtAuthResponse.setAccessToken(token);

        return jwtAuthResponse;
    }
}
