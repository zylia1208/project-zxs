package com.mango.service;

import com.mango.pojo.SmartRoom;
import com.mango.pojo.SmartSeat;
import com.mango.pojo.SmartSeatReservation;
import com.mango.pojo.SmartViolation;
import com.mango.pojo.Student;

import java.util.List;
import java.util.Map;

public interface SmartReservationService {
    List<SmartRoom> getRooms();

    SmartRoom getRoomById(String roomId);

    List<SmartSeat> getSeats(String roomId, String date, String startTime, String tag);

    List<SmartSeatReservation> getMyReservations(String studentId);

    List<SmartSeatReservation> getAllReservations(Map<String, Object> filter);

    String reserve(Student student, String seatId, String date, String startTime, String endTime);

    void cancel(String reservationId);

    String checkin(String reservationId, String code);

    String markViolation(String reservationId, Student student);

    Map<String, Object> dashboard();

    void addSeat(String roomId, String buildingId, String seatNo, String tags, String assignedMajor);

    void updateSeatStatus(String seatId, String status);

    List<SmartViolation> getViolations();
}
