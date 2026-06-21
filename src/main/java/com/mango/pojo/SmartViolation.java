package com.mango.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartViolation {
    private String violation_id;
    private String reservation_id;
    private String s_id;
    private String s_name;
    private Integer score_change;
    private String reason;
    private String created_at;
}
