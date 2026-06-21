package com.mango.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartSeat {
    private String seat_id;
    private String room_id;
    private String building_id;
    private String seat_no;
    private Integer row_index;
    private Integer col_index;
    private String tags;
    private String status;
    private String assigned_major;
    private String room_name;
    private String building_name;
    private String occupied;

    public boolean hasTag(String tag) {
        return tag == null || tag.length() == 0 || "全部".equals(tag) || (tags != null && tags.contains(tag));
    }
}
