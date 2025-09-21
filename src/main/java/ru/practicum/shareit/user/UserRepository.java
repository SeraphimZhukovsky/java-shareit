package ru.practicum.shareit.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRepository {
  private final Map<Long, User> users = new HashMap<>();
  private Long nextId = 1L;

  public User save(User user) {
    user.setId(nextId++);
    users.put(user.getId(), user);
    return user;
  }

  public Optional<User> findById(Long id) {
    return Optional.ofNullable(users.get(id));
  }

  public List<User> findAll() {
    return users.values().stream().collect(Collectors.toList());
  }

  public User update(User user) {
    users.put(user.getId(), user);
    return user;
  }

  public void deleteById(Long id) {
    users.remove(id);
  }

  public boolean existsByEmail(String email) {
    return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
  }

  public Optional<User> findByEmail(String email) {
    return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
  }
}