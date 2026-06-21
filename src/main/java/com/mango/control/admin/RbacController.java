package com.mango.control.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RbacController {

    @GetMapping("/rbac")
    public String rbac() {
        return "admin/rbac";
    }
}
