package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
  private final UserClient userClient;

  @PostMapping
  public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
    log.info("Creating user: {}", userDto);
    return userClient.createUser(userDto);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<Object> updateUser(
          @PathVariable Long userId,
          @RequestBody UserDto userDto) {
    log.info("Updating user ID: {}", userId);
    return userClient.updateUser(userId, userDto);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
    log.info("Getting user ID: {}", userId);
    return userClient.getUserById(userId);
  }

  @GetMapping
  public ResponseEntity<Object> getAllUsers() {
    log.info("Getting all users");
    return userClient.getAllUsers();
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
    log.info("Deleting user ID: {}", userId);
    return userClient.deleteUser(userId);
  }
}