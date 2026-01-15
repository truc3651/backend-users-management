package com.backend.users.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.backend.users.entities.UserEntity;
import com.backend.users.graph.UserNode;
import com.backend.users.repositories.UserNodeRepository;
import com.backend.users.repositories.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserNodeRepository userNodeRepository;
    private final UserRepository userRepository;

    public UserEntity findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public void ensureUserNodeExists(Long id) {
        UserEntity user = findUserById(id);
        ensureUserNodeExists(user);
    }

    public void ensureUserNodeExists(UserEntity user) {
        if (!userNodeRepository.existsById(user.getId())) {
            UserNode userNode = new UserNode(user.getId(), user.getEmail());
            userNodeRepository.save(userNode);
        }
    }
}
