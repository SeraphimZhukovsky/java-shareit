package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  public UserDto createUser(UserDto userDto) {
    log.info("Creating user: name={}, email={}", userDto.getName(), userDto.getEmail());

    if (userRepository.existsByEmail(userDto.getEmail())) {
      log.warn("Email already exists: {}", userDto.getEmail());
      throw new ConflictException("Email already exists");
    }

    log.info("Mapping user DTO to entity");
    User user = UserMapper.toUser(userDto);

    log.info("Saving user to database");
    User savedUser = userRepository.save(user);

    log.info("User created with ID: {}", savedUser.getId());
    return UserMapper.toUserDto(savedUser);
  }

  @Override
  public UserDto updateUser(Long userId, UserDto userDto) {
    log.info("Updating user ID: {}", userId);
    User existingUser = getUserByIdOrThrow(userId);

    if (userDto.getName() != null && !userDto.getName().isBlank()) {
      existingUser.setName(userDto.getName());
    }

    if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
      userRepository.findByEmail(userDto.getEmail())
              .ifPresent(user -> {
                if (!user.getId().equals(userId)) {
                  throw new ConflictException("Email already exists");
                }
              });
      existingUser.setEmail(userDto.getEmail());
    }

    User updatedUser = userRepository.save(existingUser);
    log.info("User ID: {} updated successfully", userId);
    return UserMapper.toUserDto(updatedUser);
  }

  private void checkEmailUniqueness(String email, Long userId) {
    if (userRepository.findByEmail(email)
            .filter(user -> !user.getId().equals(userId))
            .isPresent()) {
      throw new ConflictException("Email already exists");
    }
  }

  private boolean isValidEmail(String email) {
    return email.contains("@") && email.contains(".");
  }

  @Override
  public UserDto getUserById(Long userId) {
    log.info("Getting user ID: {}", userId);

    User user = getUserByIdOrThrow(userId);
    return UserMapper.toUserDto(user);
  }

  @Override
  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream()
            .map(UserMapper::toUserDto)
            .collect(Collectors.toList());
  }

  @Override
  public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
  }

  private User getUserByIdOrThrow(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> {
              log.warn("User not found with ID: {}", userId);
              return new NotFoundException("User with id " + userId + " not found");
            });
  }
}
