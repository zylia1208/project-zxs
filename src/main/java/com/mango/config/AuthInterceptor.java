package com.mango.config;

import com.mango.constant.WebConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && contextPath.length() > 0 && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        if (isPublicPath(path)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(WebConstant.LOGIN_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        Object permissionsObject = session.getAttribute(WebConstant.LOGIN_PERMISSIONS);
        Set<String> permissions = permissionsObject instanceof Set ? (Set<String>) permissionsObject : java.util.Collections.emptySet();
        if (isAdminViewPath(path) && !permissions.contains(WebConstant.PERMISSION_SMART_ADMIN_VIEW)) {
            response.sendRedirect(request.getContextPath() + "/student_index");
            return false;
        }
        if (isReservationViewPath(path) && !permissions.contains(WebConstant.PERMISSION_RESERVATION_VIEW)) {
            response.sendRedirect(request.getContextPath() + "/student_index");
            return false;
        }
        if (isSmartAdminPath(path) && !permissions.contains(WebConstant.PERMISSION_SMART_ADMIN_VIEW)) {
            response.sendRedirect(request.getContextPath() + "/smart_seat");
            return false;
        }
        if (isRbacPath(path) && !permissions.contains(WebConstant.PERMISSION_RBAC_MANAGE)) {
            response.sendRedirect(request.getContextPath() + "/index");
            return false;
        }
        if (isAdminManagePath(path) && !permissions.contains(WebConstant.PERMISSION_SMART_SEAT_MANAGE)) {
            response.sendRedirect(request.getContextPath() + "/smart_admin");
            return false;
        }
        return true;
    }

    private boolean isPublicPath(String path) {
        return "/".equals(path)
                || "/login".equals(path)
                || "/logincheck".equals(path)
                || path.startsWith("/assets/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/");
    }

    private boolean isSmartAdminPath(String path) {
        return path.startsWith("/smart_admin");
    }

    private boolean isRbacPath(String path) {
        return path.startsWith("/rbac");
    }

    private boolean isAdminViewPath(String path) {
        return "/index".equals(path);
    }

    private boolean isReservationViewPath(String path) {
        return "/all_student_reservation".equals(path)
                || "/all_classroom_reservation".equals(path);
    }

    private boolean isAdminManagePath(String path) {
        return "/all_student".equals(path)
                || path.startsWith("/add_new_student")
                || path.startsWith("/student_delete")
                || "/all_classroom".equals(path)
                || path.startsWith("/updateClassroomInfo")
                || path.startsWith("/classroom_delete")
                || path.startsWith("/add_new_classroom")
                || path.startsWith("/blacklist")
                || path.startsWith("/set_student_black_list")
                || path.startsWith("/blacklist_delete");
    }
}
