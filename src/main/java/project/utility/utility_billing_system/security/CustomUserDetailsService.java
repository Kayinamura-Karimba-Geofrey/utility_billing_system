package project.utility.utility_billing_system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.utility.utility_billing_system.entity.User;
import project.utility.utility_billing_system.entity.UserStatus;
import project.utility.utility_billing_system.repository.UserRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        boolean enabled = user.getStatus() == UserStatus.ACTIVE;

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                enabled, // enabled
                true,    // accountNonExpired
                true,    // credentialsNonExpired
                true,    // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
