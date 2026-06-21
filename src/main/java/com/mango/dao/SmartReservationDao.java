package com.mango.dao;

import com.mango.pojo.SmartRoom;
import com.mango.pojo.SmartSeat;
import com.mango.pojo.SmartSeatReservation;
import com.mango.pojo.SmartViolation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SmartReservationDao {
    List<SmartRoom> getSmartRooms();

    SmartRoom getSmartRoomById(String room_id);

    List<SmartSeat> getSmartSeats(Map<String, Object> map);

    SmartSeat getSmartSeatById(String seat_id);

    SmartSeatReservation getReservationById(String reservation_id);

    List<SmartSeatReservation> getMyReservations(String s_id);

    List<SmartSeatReservation> getAllReservations(Map<String, Object> map);

    int countTodayReservations();

    int countOpenRooms();

    int countViolations();

    int countRoomReservations(String room_id);

    int countRoomSeats(String room_id);

    void addReservation(SmartSeatReservation reservation);

    void updateReservationStatus(Map<String, Object> map);

    void updateStudentCredit(Map<String, Object> map);

    void addViolation(SmartViolation violation);

    List<SmartViolation> getViolations();

    void addSeat(Map<String, Object> map);

    void updateSeatStatus(Map<String, Object> map);

    Integer countSeatUsage(@Param("room_id") String room_id, @Param("reservation_date") String reservation_date);

    Integer countSeatUsageByDate(String reservation_date);
}
