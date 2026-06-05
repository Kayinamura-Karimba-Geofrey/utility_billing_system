package project.utility.utility_billing_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import project.utility.utility_billing_system.entity.Role;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private Role role;
}
