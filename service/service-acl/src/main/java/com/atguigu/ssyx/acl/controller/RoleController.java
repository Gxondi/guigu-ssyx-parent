package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.acl.service.RoleService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.acl.Role;
import com.atguigu.ssyx.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色管理")
@RestController
//@CrossOrigin
@RequestMapping("/admin/acl/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    //1.根据分页信息查询角色
    @ApiOperation(value = "分页查询角色")
    @GetMapping("{page}/{limit}")
    public Result pageList(@PathVariable Long page, @PathVariable Long limit, RoleQueryVo roleQueryVo){
        Page pageParam = new Page(page, limit);
        IPage<Role> pageModel = roleService.selectRolePage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }
    //2.根据id查询角色
    @ApiOperation(value = "根据id查询角色")
    @GetMapping("{id}")
    public Result getRoleById(@PathVariable Long id){
        Role role = roleService.getRoleById(id);
       return Result.ok(role);
    }
    //3.增加角色
    @ApiOperation(value = "增加角色")
    @PostMapping("save")
    public Result save(@RequestBody Role role){
        if ( roleService.save(role)){
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }

    }
    //4.根据id修改角色
    @ApiOperation(value = "根据id修改角色")
    @PutMapping("update")
    public Result update(@RequestBody Role role){
        roleService.updateById(role);
        if (roleService.updateById(role)){
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }
    }
    //5.根据id删除角色
    @ApiOperation(value = "根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable Long id){
        if(roleService.removeById(id)){
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }
    }
    //6.批量删除角色
    @ApiOperation(value = "批量删除角色")
    @DeleteMapping("batchRemove")
    public Result batchDelete(@RequestBody List<Long> ids){
        if(roleService.removeByIds(ids)){
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }
    }
}
