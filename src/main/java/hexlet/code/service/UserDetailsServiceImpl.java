package hexlet.code.service;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.security.UserDetailsImpl;
import hexlet.code.util.exception.UserNotFoundByEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundByEmailException(username));

        return new UserDetailsImpl(user);
    }
}
