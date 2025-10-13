package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
  List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);

  List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(Long requesterId, Pageable pageable);

  List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(Long requesterId);
}