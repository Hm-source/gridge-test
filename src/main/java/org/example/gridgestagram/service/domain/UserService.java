package org.example.gridgestagram.service.domain;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.auth.dto.SignUpRequest;
import org.example.gridgestagram.controller.auth.dto.SignUpResponse;
import org.example.gridgestagram.controller.user.dto.UserResponse;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse findById(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("유저가 데이터베이스 내 존재하지 않습니다. 유저 id : " + id));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("유저가 데이터베이스 내 존재하지 않습니다. 유저 id : " + username));
    }


    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("존재하지 않는 유저입니다 - username : " + username));
        return user;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findByName(String username) {
        List<User> users = userRepository.findByName(username);
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
            .stream()
            .map(UserResponse::from)
            .toList();
    }

    @Transactional
    public SignUpResponse save(SignUpRequest request, String url) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.createBasicUser(
            request.getUsername(),
            request.getName(),
            encodedPassword,
            request.getPhone(),
            request.getBirthdate(),
            url
        );
        User created = userRepository.save(user);
        return SignUpResponse.from(created);
    }

    @Transactional
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public void validateUsernameNotExists(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 username 입니다: " + username);
        }
    }
}
