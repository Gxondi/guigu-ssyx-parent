package com.atguigu.ssyx.acl.service;

import com.atguigu.ssyx.model.acl.Permission;
import com.atguigu.ssyx.model.acl.RolePermission;
import com.atguigu.ssyx.vo.acl.PermissionQueryVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    List<Permission> getMenuList();

    void add(PermissionQueryVo permission);

    void removeChildById(Long id);
    List<Permission> getPermissionByRoleId(Long roleId);

    void saveRolePermissionRelationShip(Long roleId,  Long[] permissionId);
}
