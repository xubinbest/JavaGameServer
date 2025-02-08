package org.xubin.login.admin;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xubin.login.node.GameNode;

/**
 * 后台管理控制器
 * @author xubin
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public String login(@RequestBody ReqAdminLogin reqLogin) {
        return adminService.login(reqLogin.getAccountId(), reqLogin.getPassword());
    }

    @GetMapping("/serverList")
    public String serverList() {
        return adminService.serverList();
    }

    @PostMapping("/updateServerInfo")
    public String updateServerInfo(@RequestBody GameNode gameNode) {
        return adminService.updateServerInfo(gameNode);
    }
}
