package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  // Методы для booker (с объектом User) - ТАК ИСПОЛЬЗУЕТСЯ В BookingServiceImpl
  List<Booking> findByBooker(User booker, Sort sort);

  List<Booking> findByBookerAndStartBeforeAndEndAfter(User booker,
                                                      LocalDateTime start,
                                                      LocalDateTime end,
                                                      Sort sort);

  List<Booking> findByBookerAndEndBefore(User booker, LocalDateTime end, Sort sort);

  List<Booking> findByBookerAndStartAfter(User booker, LocalDateTime start, Sort sort);

  List<Booking> findByBookerAndStatus(User booker, BookingStatus status, Sort sort);

  // Методы для owner (через item.ownerId)
  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId")
  List<Booking> findByItem_OwnerId(@Param("ownerId") Long ownerId, Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.start < :start AND b.end > :end")
  List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfter(@Param("ownerId") Long ownerId,
                                                            @Param("start") LocalDateTime start,
                                                            @Param("end") LocalDateTime end,
                                                            Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.end < :end")
  List<Booking> findByItem_OwnerIdAndEndBefore(@Param("ownerId") Long ownerId,
                                               @Param("end") LocalDateTime end,
                                               Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.start > :start")
  List<Booking> findByItem_OwnerIdAndStartAfter(@Param("ownerId") Long ownerId,
                                                @Param("start") LocalDateTime start,
                                                Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.status = :status")
  List<Booking> findByItem_OwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                            @Param("status") BookingStatus status,
                                            Sort sort);

  // Остальные методы
  List<Booking> findByItemIdAndStatusIn(Long itemId, List<BookingStatus> statuses, Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.end < :now ORDER BY b.end DESC")
  List<Booking> findLastBookingForItem(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

  @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.start > :now ORDER BY b.start ASC")
  List<Booking> findNextBookingForItem(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

  boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);

  @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.item.id = :itemId " +
          "AND b.end < :endTime AND b.status = :status")
  List<Booking> findByBookerIdAndItemIdAndEndBeforeAndStatus(
          @Param("bookerId") Long bookerId,
          @Param("itemId") Long itemId,
          @Param("endTime") LocalDateTime endTime,
          @Param("status") BookingStatus status);
}