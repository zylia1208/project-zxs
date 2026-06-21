package com.mango.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartRoom {
    private String room_id;
    private String room_name;
    private String building_id;
    private String building_name;
    private String building_location;
    private String room_floor;
    private String available_seat;
    private Integer seat_count;
    private Integer active_reservation_count;
}
