package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.acl.service.PermissionService;
import com.atguigu.ssyx.acl.service.RolePermissionService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.model.acl.Permission;
import com.atguigu.ssyx.model.acl.RolePermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/acl/permission")
//@CrossOrigin
public class PermissionController {
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RolePermissionService rolePermissionService;
    //1.查询菜单列表
    @ApiOperation(value = "查询菜单列表")
    @GetMapping("")
    public Result menuList() {
        //查询所有菜单
        List<Permission> list = permissionService.getMenuList();
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }
    //2.添加菜单
    @ApiOperation(value = "添加菜单")
    @PostMapping("save")
    public Result save(@RequestBody Permission permission) {
        System.out.println("permission = " + permission);
        permissionService.save(permission);
        return Result.ok(null);
    }
    //3.修改菜单
    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.ok(null);
    }
    //4.删除菜单
    @ApiOperation(value = "删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        permissionService.removeChildById(id);
        return Result.ok(null);
    }
    //5.查看某个角色的权限列表
    @ApiOperation(value = "查看角色的权限列表")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {

        List<Permission> permissionByRoleId = permissionService.getPermissionByRoleId(roleId);
        return Result.ok(permissionByRoleId);
    }
    //5.给角色分配权限
    @ApiOperation(value = "给角色分配权限")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long roleId, @RequestParam Long[] permissionId) {
        permissionService.saveRolePermissionRelationShip(roleId, permissionId);
        return Result.ok(null);
    }
}
