package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
  private final ItemRequestRepository itemRequestRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  @Override
  @Transactional
  public ItemRequestResponseDto createRequest(ItemRequestDto itemRequestDto, Long requesterId) {
    User requester = getUserById(requesterId);

    ItemRequest itemRequest = new ItemRequest();
    itemRequest.setDescription(itemRequestDto.getDescription());
    itemRequest.setRequester(requester);
    itemRequest.setCreated(LocalDateTime.now());

    ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
    log.info("Item request created with ID: {}", savedRequest.getId());

    return toItemRequestResponseDto(savedRequest);
  }

  @Override
  public List<ItemRequestResponseDto> getUserRequests(Long requesterId) {
    getUserById(requesterId);

    List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);

    return requests.stream()
            .map(this::toItemRequestResponseDto)
            .collect(Collectors.toList());
  }

  @Override
  public List<ItemRequestResponseDto> getOtherUsersRequests(Long userId, Integer from, Integer size) {
    getUserById(userId);

    Pageable pageable = PageRequest.of(from / size, size);
    List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId, pageable);

    return requests.stream()
            .map(this::toItemRequestResponseDto)
            .collect(Collectors.toList());
  }

  @Override
  public ItemRequestResponseDto getRequestById(Long requestId, Long userId) {
    getUserById(userId);

    ItemRequest itemRequest = itemRequestRepository.findById(requestId)
            .orElseThrow(() -> new NotFoundException("Item request with id " + requestId + " not found"));

    return toItemRequestResponseDto(itemRequest);
  }

  private ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest) {
    List<ItemDto> items = itemRepository.findByRequestId(itemRequest.getId()).stream()
            .map(this::toItemDto)
            .collect(Collectors.toList());

    return new ItemRequestResponseDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated(),
            items
    );
  }

  private ItemDto toItemDto(Item item) {
    return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getRequestId()
    );
  }

  private User getUserById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
  }
}