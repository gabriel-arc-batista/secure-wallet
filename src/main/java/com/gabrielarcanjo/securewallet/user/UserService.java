package com.gabrielarcanjo.securewallet.user;

import com.gabrielarcanjo.securewallet.user.dto.CreateUserRequest;
import com.gabrielarcanjo.securewallet.user.dto.UserResponse;
import com.gabrielarcanjo.securewallet.user.exception.EmailAlreadyRegisteredException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = new User(request.fullName().trim(), email, passwordHash);
        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }
}
