package com.mango.control;


import com.mango.constant.WebConstant;
import com.mango.dao.BaseDao;
import com.mango.pojo.Student;
import com.mango.service.Impl.StudentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录接口，进行登录判断
 */
@Controller
public class LoginController {

    @Autowired
    StudentServiceImpl service;

    @Autowired
    BaseDao baseDao;

    @GetMapping({"/","login"})
    public String login() {
        return "login";
    }

    @GetMapping("/logincheck")
    public String check(@RequestParam("username") String s_id, @RequestParam("password") String psw, Model model, HttpServletRequest request) {


        Student student = service.getStudentById(s_id);
        if (student == null) {
            model.addAttribute("msg","该用户不存在!");
            return "login";
        }else {
            if (psw.equals(student.getPassword())) {
                HttpSession session = request.getSession();

                session.setAttribute(WebConstant.LOGIN_USER,student);
                String role = resolveRole(student);
                session.setAttribute(WebConstant.LOGIN_ROLE, role);
                session.setAttribute(WebConstant.LOGIN_ROLE_NAME, resolveRoleName(role));
                session.setAttribute(WebConstant.LOGIN_PERMISSIONS, resolvePermissions(role));
                if (WebConstant.ROLE_ADMIN.equals(role)
                        || WebConstant.ROLE_SUPER_ADMIN.equals(role)
                        || WebConstant.ROLE_ANALYST.equals(role)) {
                    return "redirect:index";
                }else {
                    return "redirect:student_index";
                }
            } else {
                model.addAttribute("msg","用户或密码错误!");
                return "login";
            }
        }
    }
    private String resolveRole(Student student) {
        if ("super_admin".equals(student.getS_id())) {
            return WebConstant.ROLE_SUPER_ADMIN;
        }
        if ("admin".equals(student.getS_id())) {
            return WebConstant.ROLE_ADMIN;
        }
        if ("analyst".equals(student.getS_id())) {
            return WebConstant.ROLE_ANALYST;
        }
        return WebConstant.ROLE_STUDENT;
    }

    private String resolveRoleName(String role) {
        if (WebConstant.ROLE_SUPER_ADMIN.equals(role)) {
            return "系统管理员";
        }
        if (WebConstant.ROLE_ADMIN.equals(role)) {
            return "普通管理员";
        }
        if (WebConstant.ROLE_ANALYST.equals(role)) {
            return "数据分析员";
        }
        return "学生";
    }

    private Set<String> resolvePermissions(String role) {
        Set<String> permissions = new HashSet<>();
        if (WebConstant.ROLE_ADMIN.equals(role)) {
            permissions.add(WebConstant.PERMISSION_SMART_ADMIN_VIEW);
            permissions.add(WebConstant.PERMISSION_SMART_SEAT_MANAGE);
            permissions.add(WebConstant.PERMISSION_RESERVATION_VIEW);
            permissions.add(WebConstant.PERMISSION_VIOLATION_VIEW);
        } else if (WebConstant.ROLE_SUPER_ADMIN.equals(role)) {
            permissions.add(WebConstant.PERMISSION_SMART_ADMIN_VIEW);
            permissions.add(WebConstant.PERMISSION_SMART_SEAT_MANAGE);
            permissions.add(WebConstant.PERMISSION_RESERVATION_VIEW);
            permissions.add(WebConstant.PERMISSION_VIOLATION_VIEW);
            permissions.add(WebConstant.PERMISSION_RBAC_MANAGE);
        } else if (WebConstant.ROLE_ANALYST.equals(role)) {
            permissions.add(WebConstant.PERMISSION_SMART_ADMIN_VIEW);
            permissions.add(WebConstant.PERMISSION_RESERVATION_VIEW);
            permissions.add(WebConstant.PERMISSION_VIOLATION_VIEW);
        }
        return permissions;
    }
}
