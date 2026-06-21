package com.mango.control.smart;

import com.mango.constant.WebConstant;
import com.mango.pojo.SmartRoom;
import com.mango.pojo.SmartSeat;
import com.mango.pojo.SmartSeatReservation;
import com.mango.pojo.Student;
import com.mango.service.SmartReservationService;
import com.mango.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class SmartReservationController {

    @Autowired
    private SmartReservationService smartReservationService;

    @GetMapping("/smart_seat")
    public String smartSeat(HttpServletRequest request,
                            Model model,
                            @RequestParam(value = "room_id", required = false) String roomId,
                            @RequestParam(value = "reservation_date", required = false) String reservationDate,
                            @RequestParam(value = "start_time", required = false) String startTime,
                            @RequestParam(value = "end_time", required = false) String endTime,
                            @RequestParam(value = "tag", required = false) String tag,
                            @RequestParam(value = "assistant_query", required = false) String assistantQuery) {
        Student loginUser = CommonUtil.getLoginUser(request);
        List<SmartRoom> rooms = smartReservationService.getRooms();
        if ((roomId == null || roomId.length() == 0) && rooms.size() > 0) {
            roomId = rooms.get(0).getRoom_id();
        }
        if (reservationDate == null || reservationDate.length() == 0) {
            reservationDate = LocalDate.now().toString();
        }
        if (startTime == null || startTime.length() == 0) {
            startTime = "18:00:00";
        }
        if (endTime == null || endTime.length() == 0) {
            endTime = "20:00:00";
        }
        if (tag == null || tag.length() == 0) {
            tag = "全部";
        }

        List<String> assistantTags = parseAssistantTags(assistantQuery);
        boolean assistantSeatQuery = isAssistantSeatQuery(assistantQuery, assistantTags);
        List<SmartSeat> seats = smartReservationService.getSeats(roomId, reservationDate, startTime, assistantSeatQuery ? "全部" : tag);
        List<SmartSeatReservation> reservations = smartReservationService.getMyReservations(loginUser.getS_id());
        Set<String> assistantMatchedSeatIds = assistantSeatQuery ? matchedSeatIds(seats, assistantTags) : new HashSet<>();
        String assistantResult = buildAssistantResult(assistantQuery, reservations, seats, assistantTags, assistantMatchedSeatIds, assistantSeatQuery);

        model.addAttribute("rooms", rooms);
        model.addAttribute("seats", seats);
        model.addAttribute("reservations", reservations);
        model.addAttribute("dashboard", smartReservationService.dashboard());
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("reservationDate", reservationDate);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("tag", tag);
        model.addAttribute("assistantQuery", assistantQuery);
        model.addAttribute("assistantResult", assistantResult);
        model.addAttribute("assistantTags", assistantTags);
        model.addAttribute("assistantMatchedSeatIds", assistantMatchedSeatIds);
        model.addAttribute("assistantSeatQuery", assistantSeatQuery);
        model.addAttribute("creditScore", loginUser.getCredit_score() == null ? 5 : loginUser.getCredit_score());
        return "smart/seat_reservation";
    }

    @PostMapping("/smart_seat/reserve")
    public String reserve(HttpServletRequest request,
                          @RequestParam("seat_id") String seatId,
                          @RequestParam("reservation_date") String reservationDate,
                          @RequestParam("start_time") String startTime,
                          @RequestParam("end_time") String endTime,
                          RedirectAttributes redirectAttributes) {
        Student loginUser = CommonUtil.getLoginUser(request);
        String msg = smartReservationService.reserve(loginUser, seatId, reservationDate, startTime, endTime);
        redirectAttributes.addFlashAttribute("msg", msg);
        return "redirect:/smart_seat";
    }

    @PostMapping("/smart_seat/cancel")
    public String cancel(@RequestParam("reservation_id") String reservationId, RedirectAttributes redirectAttributes) {
        smartReservationService.cancel(reservationId);
        redirectAttributes.addFlashAttribute("msg", "预约已取消");
        return "redirect:/smart_seat";
    }

    @PostMapping("/smart_seat/checkin")
    public String checkin(@RequestParam("reservation_id") String reservationId,
                          @RequestParam("checkin_code") String checkinCode,
                          RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("msg", smartReservationService.checkin(reservationId, checkinCode));
        return "redirect:/smart_seat";
    }

    @PostMapping("/smart_seat/timeout")
    public String timeout(HttpServletRequest request,
                          @RequestParam("reservation_id") String reservationId,
                          RedirectAttributes redirectAttributes) {
        Student loginUser = CommonUtil.getLoginUser(request);
        redirectAttributes.addFlashAttribute("msg", smartReservationService.markViolation(reservationId, loginUser));
        return "redirect:/smart_seat";
    }

    @GetMapping("/smart_admin")
    public String smartAdmin(HttpServletRequest request,
                             Model model,
                             @RequestParam(value = "s_id", required = false) String studentId,
                             @RequestParam(value = "status", required = false) String status) {
        Student loginUser = CommonUtil.getLoginUser(request);
        if (loginUser == null || !CommonUtil.hasPermission(request, WebConstant.PERMISSION_SMART_ADMIN_VIEW)) {
            return "redirect:/smart_seat";
        }
        String role = CommonUtil.getLoginRole(request);
        boolean canManageSeat = CommonUtil.hasPermission(request, WebConstant.PERMISSION_SMART_SEAT_MANAGE);
        Map<String, Object> filter = new HashMap<>();
        filter.put("s_id", studentId);
        filter.put("status", status);
        List<SmartRoom> rooms = smartReservationService.getRooms();
        List<SmartSeat> seats = rooms.size() == 0 ? java.util.Collections.emptyList() : smartReservationService.getSeats(rooms.get(0).getRoom_id(), LocalDate.now().toString(), "08:00:00", "全部");
        model.addAttribute("role", role);
        model.addAttribute("roleName", request.getSession().getAttribute(WebConstant.LOGIN_ROLE_NAME));
        model.addAttribute("canManageSeat", canManageSeat);
        model.addAttribute("rooms", rooms);
        model.addAttribute("seats", seats);
        model.addAttribute("reservations", smartReservationService.getAllReservations(filter));
        model.addAttribute("violations", smartReservationService.getViolations());
        model.addAttribute("dashboard", smartReservationService.dashboard());
        model.addAttribute("studentId", studentId);
        model.addAttribute("status", status);
        return "smart/admin";
    }

    @PostMapping("/smart_admin/add_seat")
    public String addSeat(HttpServletRequest request,
                          @RequestParam("room_id") String roomId,
                          @RequestParam("building_id") String buildingId,
                          @RequestParam("seat_no") String seatNo,
                          @RequestParam(value = "tags", required = false) String tags,
                          @RequestParam(value = "assigned_major", required = false) String assignedMajor,
                          RedirectAttributes redirectAttributes) {
        if (!CommonUtil.hasPermission(request, WebConstant.PERMISSION_SMART_SEAT_MANAGE)) {
            redirectAttributes.addFlashAttribute("msg", "RBAC拦截：数据分析员只有只读权限");
            return "redirect:/smart_admin";
        }
        smartReservationService.addSeat(roomId, buildingId, seatNo, tags, assignedMajor);
        redirectAttributes.addFlashAttribute("msg", "座位新增成功");
        return "redirect:/smart_admin";
    }

    @PostMapping("/smart_admin/toggle_seat")
    public String toggleSeat(HttpServletRequest request,
                             @RequestParam("seat_id") String seatId,
                             @RequestParam("status") String status,
                             RedirectAttributes redirectAttributes) {
        if (!CommonUtil.hasPermission(request, WebConstant.PERMISSION_SMART_SEAT_MANAGE)) {
            redirectAttributes.addFlashAttribute("msg", "RBAC拦截：数据分析员只有只读权限");
            return "redirect:/smart_admin";
        }
        smartReservationService.updateSeatStatus(seatId, status);
        redirectAttributes.addFlashAttribute("msg", "座位状态已更新");
        return "redirect:/smart_admin";
    }

    private String buildAssistantResult(String query,
                                        List<SmartSeatReservation> reservations,
                                        List<SmartSeat> seats,
                                        List<String> assistantTags,
                                        Set<String> assistantMatchedSeatIds,
                                        boolean assistantSeatQuery) {
        if (query == null || query.trim().length() == 0) {
            return null;
        }
        if (query.contains("今天") && query.contains("预约")) {
            int pending = 0;
            for (SmartSeatReservation reservation : reservations) {
                if (LocalDate.now().toString().equals(reservation.getReservation_date()) && "待签到".equals(reservation.getStatus())) {
                    pending++;
                }
            }
            return pending == 0 ? "今天暂无待签到预约" : "你今天有 " + pending + " 条待签到预约";
        }
        if (!assistantSeatQuery) {
            int available = 0;
            for (SmartSeat seat : seats) {
                if ("active".equals(seat.getStatus()) && "0".equals(seat.getOccupied())) {
                    available++;
                }
            }
            return "当前筛选条件下还有 " + available + " 个可预约座位";
        }
        int available = 0;
        for (SmartSeat seat : seats) {
            if (assistantMatchedSeatIds.contains(seat.getSeat_id())) {
                available++;
            }
        }
        String condition = assistantTags.size() == 0 ? "当前条件" : String.join("、", assistantTags);
        return "找到 " + available + " 个符合“" + condition + "”的可预约座位，已在下方座位分布图中高亮显示";
    }

    private List<String> parseAssistantTags(String query) {
        List<String> tags = new ArrayList<>();
        if (query == null || query.trim().length() == 0) {
            return tags;
        }
        if (query.contains("插座") || query.contains("充电") || query.contains("电源")) {
            tags.add("插座");
        }
        if (query.contains("靠窗") || query.contains("窗边") || query.contains("窗户")) {
            tags.add("靠窗");
        }
        if (query.contains("安静") || query.contains("静音")) {
            tags.add("安静区");
        }
        if (query.contains("院系") || query.contains("专属")) {
            tags.add("院系专属");
        }
        if (query.contains("走道") || query.contains("过道")) {
            tags.add("走道");
        }
        return tags;
    }

    private boolean isAssistantSeatQuery(String query, List<String> assistantTags) {
        if (query == null || query.trim().length() == 0) {
            return false;
        }
        if (query.contains("今天") && query.contains("预约")) {
            return false;
        }
        return assistantTags.size() > 0
                || query.contains("座位")
                || query.contains("空位")
                || query.contains("空座")
                || query.contains("位置")
                || query.contains("自习");
    }

    private Set<String> matchedSeatIds(List<SmartSeat> seats, List<String> assistantTags) {
        Set<String> ids = new HashSet<>();
        for (SmartSeat seat : seats) {
            if (!"active".equals(seat.getStatus()) || !"0".equals(seat.getOccupied())) {
                continue;
            }
            boolean matched = true;
            for (String assistantTag : assistantTags) {
                if (!seat.hasTag(assistantTag)) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                ids.add(seat.getSeat_id());
            }
        }
        return ids;
    }
}
