package com.tripgg.schedule.repository;

import com.tripgg.schedule.entity.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {

    @Query("SELECT si FROM ScheduleItem si " +
            "JOIN si.schedule s " +
            "LEFT JOIN FETCH si.schedulePlaces " +
            "WHERE s.user.id = :userId " +
            "AND si.startDate = CURRENT_DATE " +
            "ORDER BY si.day, si.orderInDay")
    List<ScheduleItem> findTodayStartScheduleItems(@Param("userId") Long userId);

}
