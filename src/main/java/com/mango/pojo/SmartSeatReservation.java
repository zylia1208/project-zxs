package com.mango.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartSeatReservation {
    private String reservation_id;
    private String s_id;
    private String s_name;
    private String seat_id;
    private String seat_no;
    private String room_id;
    private String room_name;
    private String building_id;
    private String building_name;
    private String reservation_date;
    private String start_time;
    private String end_time;
    private String status;
    private String checkin_code;
    private String checkin_time;
    private String created_at;
}
