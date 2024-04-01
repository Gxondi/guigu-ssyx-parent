package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.acl.service.AdminService;
import com.atguigu.ssyx.acl.service.RoleService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.common.utils.MD5;
import com.atguigu.ssyx.model.acl.Admin;
import com.atguigu.ssyx.model.acl.Role;
import com.atguigu.ssyx.vo.acl.AdminQueryVo;
import com.atguigu.ssyx.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/acl/user")

public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;
    //1.根据分页信息查询用户
    @ApiOperation(value = "分页查询用户查询")
    @GetMapping("{page}/{limit}")
    public Result pageList(@PathVariable Long page, @PathVariable Long limit, AdminQueryVo adminQueryVo){
        Page pageParam = new Page(page, limit);
        IPage<Admin> pageModel = adminService.selectAdminPage(pageParam, adminQueryVo);
        return Result.ok(pageModel);
    }
    //2.根据id查询用户
    @ApiOperation(value = "根据id查询用户")
    @GetMapping("{id}")
    public Result getById(@PathVariable Long id){
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }
    //3.增加用户
    @ApiOperation(value = "增加用户")
    @PostMapping("save")
    public Result save(@RequestBody Admin admin){
        String password = admin.getPassword();
        String newPassword = MD5.encrypt(password);
        admin.setPassword(newPassword);
        if (adminService.save(admin)) {
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }

    }
    //4.根据id修改用户
    @ApiOperation(value = "根据id修改用户")
    @PutMapping("update")
    public Result update(@RequestBody Admin admin){
        if (adminService.updateById(admin)) {
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }
    }
    //5.根据id删除用户
    @ApiOperation(value = "根据id删除用户")
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable Long id){
        if (adminService.removeById(id)) {
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }
    }
    //6.批量删除用户
    @ApiOperation(value = "批量删除用户")
    @DeleteMapping("batchRemove")
    public Result batchDelete(@RequestBody List<Long> ids){
        if (adminService.removeByIds(ids)) {
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }
    }
    //7.获取当前用户的角色
    @ApiOperation(value = "获取当前用户的所有角色")
    @GetMapping("toAssign/{adminId}")
    public Result getRoles(@PathVariable("adminId") Long adminId){
        Map<String, Object> map = roleService.getRolesByAdminId(adminId);
        return Result.ok(map);
    }
    //8.分配角色
    @ApiOperation(value = "给当前用户分配角色")
    @PostMapping("doAssign")
    public Result assignRoles(@RequestParam Long adminId, @RequestParam Long[] roleId){
        roleService.saveAdminRole(adminId, roleId);
        return Result.ok(null);
    }
}
