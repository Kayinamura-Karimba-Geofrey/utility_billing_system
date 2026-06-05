package project.utility.utility_billing_system.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import project.utility.utility_billing_system.dto.AuthResponse;
import project.utility.utility_billing_system.dto.LoginRequest;
import project.utility.utility_billing_system.dto.SignupRequest;
import project.utility.utility_billing_system.entity.User;
import project.utility.utility_billing_system.entity.UserStatus;
import project.utility.utility_billing_system.repository.UserRepository;
import project.utility.utility_billing_system.security.JwtUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .fullNames(signUpRequest.getFullNames())
                .email(signUpRequest.getEmail())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .status(UserStatus.ACTIVE)
                .role(signUpRequest.getRole())
                .build();

        userRepository.save(user);

        String jwt = jwtUtils.generateJwtToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(jwt, user.getEmail(), user.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(loginRequest.getEmail());
        
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        
        return ResponseEntity.ok(new AuthResponse(jwt, user.getEmail(), user.getRole()));
    }
}
