package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  List<Booking> findByBookerId(Long bookerId, Sort sort);

  List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                        LocalDateTime start,
                                                        LocalDateTime end,
                                                        Sort sort);

  List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

  List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

  List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId")
  List<Booking> findByItemOwnerId(@Param("ownerId") Long ownerId, Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.start < :start AND b.end > :end")
  List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(@Param("ownerId") Long ownerId,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end,
                                                           Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.end < :end")
  List<Booking> findByItemOwnerIdAndEndBefore(@Param("ownerId") Long ownerId,
                                              @Param("end") LocalDateTime end,
                                              Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.start > :start")
  List<Booking> findByItemOwnerIdAndStartAfter(@Param("ownerId") Long ownerId,
                                               @Param("start") LocalDateTime start,
                                               Sort sort);

  @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.status = :status")
  List<Booking> findByItemOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                           @Param("status") BookingStatus status,
                                           Sort sort);

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
