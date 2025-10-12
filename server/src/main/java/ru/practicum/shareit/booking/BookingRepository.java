package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  // Методы для booker
  List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

  List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
          Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

  List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

  List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

  List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

  // Методы для owner
  List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

  List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
          Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

  List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable pageable);

  List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start, Pageable pageable);

  List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

  // Кастомные запросы для дат бронирований
  @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.start < ?2 ORDER BY b.start DESC")
  List<Booking> findLastBookingForItem(Long itemId, LocalDateTime now);

  @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.start > ?2 ORDER BY b.start ASC")
  List<Booking> findNextBookingForItem(Long itemId, LocalDateTime now);

  List<Booking> findByBookerIdAndItemIdAndEndBefore(
          Long bookerId, Long itemId, LocalDateTime end);

  // Получить подтвержденные бронирования для списка вещей
  @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.status = 'APPROVED' ORDER BY b.start DESC")
  List<Booking> findApprovedBookingsForItems(@Param("itemIds") List<Long> itemIds);
}