package com.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.example.entities.UserEntity;
import com.example.graph.UserNode;
import com.example.repositories.UserNodeRepository;
import com.example.repositories.UserRepository;

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
