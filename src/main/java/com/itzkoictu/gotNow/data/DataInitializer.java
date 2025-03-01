package com.itzkoictu.gotNow.data;

import com.itzkoictu.gotNow.model.Role;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.repository.RoleRepository;
import com.itzkoictu.gotNow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRole= Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_CUSTOMER");
        createDefaultRole(defaultRole);
        createDefaultAdminIfNotExits();
        createDefaultUserIfNotExists();
    }

    private void createDefaultRole(Set<String> roles){
        roles.stream().filter(role -> Optional.ofNullable(roleRepository.findByName(role))
                .isEmpty()).map(Role :: new).forEach(roleRepository::save);
    }

    private void createDefaultAdminIfNotExits(){
        Role adminRole = Optional.ofNullable(roleRepository.findByName("ROLE_ADMIN"))
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        for (int i = 1; i<=3; i++){
            String defaultEmail = "admin"+i+"@email.com";
            if (userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Shop" + i);
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles(Set.of(adminRole));
            userRepository.save(user);
        }



    }

    private void createDefaultUserIfNotExists(){
        Role userRole = Optional.ofNullable(roleRepository.findByName("ROLE_USER"))
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        for (int i = 1; i<=3; i++){
            String defaultEmail = "longne"+i+"@email.com";
            if (userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            User user = new User();
            user.setFirstName("Long");
            user.setLastName("Nguyen" + i);
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        }




    }


}
