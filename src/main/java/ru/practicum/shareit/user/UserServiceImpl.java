package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDto createUser(UserDto userDto) {
    if (userRepository.existsByEmail(userDto.getEmail())) {
      throw new ConflictException("Email already exists");
    }
    User user = UserMapper.toUser(userDto);
    User savedUser = userRepository.save(user);
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
      checkEmailUniqueness(userDto.getEmail(), userId);

      if (!isValidEmail(userDto.getEmail())) {
        throw new ValidationException("Email should be valid");
      }
      existingUser.setEmail(userDto.getEmail());
    }

    User updatedUser = userRepository.update(existingUser);
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
