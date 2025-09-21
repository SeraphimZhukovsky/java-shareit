package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
  private final UserRepository userRepository = new UserRepository();

  public UserDto createUser(UserDto userDto) {
    if (userRepository.existsByEmail(userDto.getEmail())) {
      throw new ConflictException("Email already exists");
    }
    User user = UserMapper.toUser(userDto);
    User savedUser = userRepository.save(user);
    return UserMapper.toUserDto(savedUser);
  }

  public UserDto updateUser(Long userId, UserDto userDto) {
    User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

    if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
      if (!isValidEmail(userDto.getEmail())) {
        throw new ValidationException("Email should be valid");
      }

      if (userRepository.findByEmail(userDto.getEmail())
              .filter(user -> !user.getId().equals(userId))
              .isPresent()) {
        throw new ConflictException("Email already exists");
      }
      existingUser.setEmail(userDto.getEmail());
    } else if (userDto.getEmail() != null && userDto.getEmail().isBlank()) {
      throw new ValidationException("Email cannot be blank");
    }

    if (userDto.getName() != null) {
      if (userDto.getName().isBlank()) {
        throw new ValidationException("Name cannot be blank");
      }
      existingUser.setName(userDto.getName());
    }

    User updatedUser = userRepository.update(existingUser);
    return UserMapper.toUserDto(updatedUser);
  }

  private boolean isValidEmail(String email) {
    return email.contains("@") && email.contains(".");
  }

  public UserDto getUserById(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
    return UserMapper.toUserDto(user);
  }

  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream()
            .map(UserMapper::toUserDto)
            .collect(Collectors.toList());
  }

  public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
  }
}
