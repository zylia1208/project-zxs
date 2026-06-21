package com.mango.utils;

import com.mango.constant.WebConstant;
import com.mango.pojo.Student;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;


/**
 * 通用工具类
 * 获取当前用户登录信息id等
 */
@Component("CommonUtil")
public class CommonUtil {
    public static Student getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Student) session.getAttribute(WebConstant.LOGIN_USER);
    }

    public static String getLoginRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return WebConstant.ROLE_STUDENT;
        }
        Object role = session.getAttribute(WebConstant.LOGIN_ROLE);
        return role == null ? WebConstant.ROLE_STUDENT : role.toString();
    }

    @SuppressWarnings("unchecked")
    public static boolean hasPermission(HttpServletRequest request, String permission) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object permissions = session.getAttribute(WebConstant.LOGIN_PERMISSIONS);
        if (!(permissions instanceof Set)) {
            return false;
        }
        return ((Set<String>) permissions).contains(permission);
    }

    public static String getDateFormat(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String re = null;
        try {
            re = sdf2.format(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return re;
    }


    public static String toUseRate(String reservation_num, String available_seat) {

        Integer reservationNum = Integer.parseInt(reservation_num);
        Integer availableSeat = Integer.parseInt(available_seat);

        Double re = reservationNum.doubleValue() / (reservationNum + availableSeat) * 100;
        String use_rate = re.toString();
        use_rate = use_rate.substring(0,use_rate.indexOf(".")+3) + "%";

        return use_rate;
    }
}
