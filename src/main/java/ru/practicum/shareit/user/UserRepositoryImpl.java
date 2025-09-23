package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
  private final Map<Long, User> users = new HashMap<>();
  private Long nextId = 1L;

  @Override
  public User save(User user) {
    user.setId(nextId++);
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public Optional<User> findById(Long id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public List<User> findAll() {
    return users.values().stream().collect(Collectors.toList());
  }

  @Override
  public User update(User user) {
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public void deleteById(Long id) {
    users.remove(id);
  }

  @Override
  public boolean existsByEmail(String email) {
    return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
  }
}