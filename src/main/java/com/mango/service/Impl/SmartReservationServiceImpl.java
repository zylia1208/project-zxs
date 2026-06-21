package com.mango.service.Impl;

import com.mango.dao.SmartReservationDao;
import com.mango.pojo.SmartRoom;
import com.mango.pojo.SmartSeat;
import com.mango.pojo.SmartSeatReservation;
import com.mango.pojo.SmartViolation;
import com.mango.pojo.Student;
import com.mango.service.SmartReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SmartReservationServiceImpl implements SmartReservationService {

    private static final String STATUS_PENDING = "待签到";
    private static final String STATUS_CHECKED = "已签到";
    private static final String STATUS_CANCELED = "已取消";
    private static final String STATUS_VIOLATION = "违约";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SmartReservationDao smartReservationDao;

    @Override
    public List<SmartRoom> getRooms() {
        return smartReservationDao.getSmartRooms();
    }

    @Override
    public SmartRoom getRoomById(String roomId) {
        return smartReservationDao.getSmartRoomById(roomId);
    }

    @Override
    public List<SmartSeat> getSeats(String roomId, String date, String startTime, String tag) {
        Map<String, Object> map = new HashMap<>();
        map.put("room_id", roomId);
        map.put("reservation_date", date);
        map.put("start_time", startTime);
        map.put("tag", tag);
        return smartReservationDao.getSmartSeats(map);
    }

    @Override
    public List<SmartSeatReservation> getMyReservations(String studentId) {
        return smartReservationDao.getMyReservations(studentId);
    }

    @Override
    public List<SmartSeatReservation> getAllReservations(Map<String, Object> filter) {
        return smartReservationDao.getAllReservations(filter);
    }

    @Override
    public String reserve(Student student, String seatId, String date, String startTime, String endTime) {
        if (student == null) {
            return "请先登录";
        }
        int creditScore = student.getCredit_score() == null ? 5 : student.getCredit_score();
        if (creditScore <= 0) {
            return "违约积分为0，预约权限已暂停";
        }

        SmartSeat seat = smartReservationDao.getSmartSeatById(seatId);
        if (seat == null) {
            return "座位不存在";
        }
        if (!"active".equals(seat.getStatus())) {
            return "座位已停用，无法预约";
        }
        if (seat.getAssigned_major() != null && seat.getAssigned_major().length() > 0
                && !seat.getAssigned_major().equals(student.getS_major())) {
            return "该座位为" + seat.getAssigned_major() + "专属座位";
        }

        List<SmartSeat> seats = getSeats(seat.getRoom_id(), date, startTime, "全部");
        for (SmartSeat item : seats) {
            if (seatId.equals(item.getSeat_id()) && "1".equals(item.getOccupied())) {
                return "该座位当前时段已被预约";
            }
        }

        SmartSeatReservation reservation = new SmartSeatReservation();
        reservation.setReservation_id("SR" + System.currentTimeMillis());
        reservation.setS_id(student.getS_id());
        reservation.setSeat_id(seat.getSeat_id());
        reservation.setSeat_no(seat.getSeat_no());
        reservation.setRoom_id(seat.getRoom_id());
        reservation.setRoom_name(seat.getRoom_name());
        reservation.setBuilding_id(seat.getBuilding_id());
        reservation.setBuilding_name(seat.getBuilding_name());
        reservation.setReservation_date(date);
        reservation.setStart_time(startTime);
        reservation.setEnd_time(endTime);
        reservation.setStatus(STATUS_PENDING);
        reservation.setCheckin_code(String.valueOf(10000 + new Random().nextInt(90000)));
        reservation.setCreated_at(now());
        smartReservationDao.addReservation(reservation);
        return "预约成功，动态签到码：" + reservation.getCheckin_code();
    }

    @Override
    public void cancel(String reservationId) {
        Map<String, Object> map = new HashMap<>();
        map.put("reservation_id", reservationId);
        map.put("status", STATUS_CANCELED);
        map.put("checkin_time", null);
        smartReservationDao.updateReservationStatus(map);
    }

    @Override
    public String checkin(String reservationId, String code) {
        SmartSeatReservation reservation = smartReservationDao.getReservationById(reservationId);
        if (reservation == null) {
            return "预约记录不存在";
        }
        if (!STATUS_PENDING.equals(reservation.getStatus())) {
            return "当前预约状态不可签到";
        }
        if (code == null || !code.equals(reservation.getCheckin_code())) {
            return "动态验证码不正确";
        }

        Map<String, Object> map = new HashMap<>();
        map.put("reservation_id", reservationId);
        map.put("status", STATUS_CHECKED);
        map.put("checkin_time", now());
        smartReservationDao.updateReservationStatus(map);
        return "签到成功";
    }

    @Override
    public String markViolation(String reservationId, Student student) {
        SmartSeatReservation reservation = smartReservationDao.getReservationById(reservationId);
        if (reservation == null) {
            return "预约记录不存在";
        }
        if (!STATUS_PENDING.equals(reservation.getStatus())) {
            return "当前预约状态不可标记违约";
        }

        Map<String, Object> status = new HashMap<>();
        status.put("reservation_id", reservationId);
        status.put("status", STATUS_VIOLATION);
        status.put("checkin_time", null);
        smartReservationDao.updateReservationStatus(status);

        int currentScore = student.getCredit_score() == null ? 5 : student.getCredit_score();
        int nextScore = Math.max(0, currentScore - 1);
        Map<String, Object> credit = new HashMap<>();
        credit.put("s_id", reservation.getS_id());
        credit.put("credit_score", nextScore);
        smartReservationDao.updateStudentCredit(credit);
        student.setCredit_score(nextScore);

        SmartViolation violation = new SmartViolation();
        violation.setViolation_id("V" + System.currentTimeMillis());
        violation.setReservation_id(reservationId);
        violation.setS_id(reservation.getS_id());
        violation.setS_name(student.getS_name());
        violation.setScore_change(-1);
        violation.setReason("预约开始后20分钟仍未签到，系统自动释放座位");
        violation.setCreated_at(now());
        smartReservationDao.addViolation(violation);
        return "已记为违约，当前积分：" + nextScore;
    }

    @Override
    public Map<String, Object> dashboard() {
        Map<String, Object> map = new HashMap<>();
        List<SmartRoom> rooms = smartReservationDao.getSmartRooms();
        int seatCount = 0;
        for (SmartRoom room : rooms) {
            seatCount += room.getSeat_count() == null ? 0 : room.getSeat_count();
        }
        int todayReservations = smartReservationDao.countTodayReservations();
        int utilization = seatCount == 0 ? 0 : Math.round(todayReservations * 100f / seatCount);
        map.put("rooms", rooms);
        map.put("todayReservations", todayReservations);
        map.put("openRooms", smartReservationDao.countOpenRooms());
        map.put("violations", smartReservationDao.countViolations());
        map.put("utilization", utilization);
        return map;
    }

    @Override
    public void addSeat(String roomId, String buildingId, String seatNo, String tags, String assignedMajor) {
        Map<String, Object> map = new HashMap<>();
        map.put("seat_id", roomId + "-" + seatNo);
        map.put("room_id", roomId);
        map.put("building_id", buildingId);
        map.put("seat_no", seatNo);
        int currentSeatCount = smartReservationDao.countRoomSeats(roomId);
        map.put("row_index", currentSeatCount / 6 + 1);
        map.put("col_index", currentSeatCount % 6 + 1);
        map.put("tags", tags);
        map.put("status", "active");
        map.put("assigned_major", assignedMajor);
        smartReservationDao.addSeat(map);
    }

    @Override
    public void updateSeatStatus(String seatId, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("seat_id", seatId);
        map.put("status", status);
        smartReservationDao.updateSeatStatus(map);
    }

    @Override
    public List<SmartViolation> getViolations() {
        return smartReservationDao.getViolations();
    }

    private String now() {
        return LocalDateTime.now().format(DATE_TIME);
    }
}
