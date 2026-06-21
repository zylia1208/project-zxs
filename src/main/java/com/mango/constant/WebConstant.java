package com.mango.constant;


import org.springframework.stereotype.Component;

@Component
public class WebConstant {


    //当前登陆用户
    public static final String LOGIN_USER = "login_user";

    //当前登录用户角色和权限
    public static final String LOGIN_ROLE = "login_role";
    public static final String LOGIN_ROLE_NAME = "login_role_name";
    public static final String LOGIN_PERMISSIONS = "login_permissions";

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_SUPER_ADMIN = "super_admin";
    public static final String ROLE_ANALYST = "analyst";

    public static final String PERMISSION_SMART_ADMIN_VIEW = "smart_admin:view";
    public static final String PERMISSION_SMART_SEAT_MANAGE = "smart_seat:manage";
    public static final String PERMISSION_RESERVATION_VIEW = "reservation:view";
    public static final String PERMISSION_VIOLATION_VIEW = "violation:view";
    public static final String PERMISSION_RBAC_MANAGE = "rbac:manage";

    //学生预约信息成功状态
    public static final String RESERVATION_SUCCESS_STATE = "预约成功";

    //学生预约信息取消状态
    public static final String RESERVATION_CANCELED_STATE = "预约取消";

    //学生黑名单正常状态
    public static final String BLACKED_SUCCESS_STATE = "拉黑";

    //学生黑名单已失效状态
    public static final String BLACKED_CANCELED_STATE = "已失效";

}
