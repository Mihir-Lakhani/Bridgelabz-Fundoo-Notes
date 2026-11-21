package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.dto.ProfileResponse;
import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public ProfileResponse profile(String email) {
        return response(findUser(email));
    }

    public ProfileResponse updateName(String email, String name) {
        AppUser user = findUser(email);
        user.setName(name);
        return response(users.save(user));
    }

    private AppUser findUser(String email) {
        return users.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private ProfileResponse response(AppUser user) {
        return new ProfileResponse(user.getId(), user.getName(), user.getEmail());
    }
}
