package com.atguigu.ssyx.acl.service.Impl;

import com.atguigu.ssyx.acl.helper.PermissionHelper;
import com.atguigu.ssyx.acl.mapper.PermissionMapper;
import com.atguigu.ssyx.acl.mapper.RolePermissionMapper;
import com.atguigu.ssyx.acl.service.PermissionService;
import com.atguigu.ssyx.acl.service.RolePermissionService;
import com.atguigu.ssyx.model.acl.Permission;
import com.atguigu.ssyx.model.acl.RolePermission;
import com.atguigu.ssyx.vo.acl.PermissionQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Override
    public List<Permission> getMenuList() {
        //获取所有数据
        List<Permission> allPermissionList = baseMapper.selectList(new QueryWrapper<Permission>().orderByAsc("CAST(id AS SIGNED)"));
        //将所有数据传入菜单帮助类中
        List<Permission> result = PermissionHelper.build(allPermissionList);
        return result;
    }

    @Override
    public void add(PermissionQueryVo permission) {
        //获取当前菜单的id
        Long id = permission.getParentId();
        //生成一个新的菜单
        Permission newPermission = new Permission();
        newPermission.setPid(id);
        newPermission.setName(permission.getName());
        newPermission.setCode(permission.getCode());
        newPermission.setType(permission.getType());
        baseMapper.insert(newPermission);
    }

    @Override
    public void removeChildById(Long id) {
        List<Long> idList = new ArrayList<>();
        findChildren(id,idList);
        //将当前菜单的id添加到列表中
        idList.add(id);
        baseMapper.deleteBatchIds(idList);
    }

    @Override
    public List<Permission> getPermissionByRoleId(Long roleId) {
        //查询所有菜单
        List<Permission> permissionList = baseMapper.selectList(new QueryWrapper<Permission>().orderByAsc("CAST(id AS SIGNED)"));
        //查询当前角色的权限
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> isSelectedList = rolePermissionMapper.selectList(wrapper);
        List<Long> isSelectedListIds = isSelectedList.stream().map(item -> {
            return item.getPermissionId();
        }).collect(Collectors.toList());

        //将当前角色的权限标记为选中
        for (Permission permission : permissionList) {
            if (isSelectedListIds.contains(permission.getId())){
                permission.setSelect(true);
            }
        }
        List<Permission> allPermissionList = PermissionHelper.build(permissionList);
        return allPermissionList;
    }

    @Override
    public void saveRolePermissionRelationShip(Long roleId,  Long[] permissionId) {

        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper();
        wrapper.eq(RolePermission::getRoleId, roleId);
        //删除当前角色的所有权限
        rolePermissionMapper.delete(wrapper);
        //保存新的权限
        for (Long id : permissionId) {
            RolePermission newRolePermission = new RolePermission();
            newRolePermission.setRoleId(roleId);
            newRolePermission.setPermissionId(id);
            rolePermissionMapper.insert(newRolePermission);
        }
    }

    /**
     * 递归删除菜单
     * @param id
     * @param idList
     */
    private void findChildren(Long id, List<Long> idList) {
        //获取当前菜单的子菜单
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid, id);

        List<Permission> childrenList = baseMapper.selectList(wrapper);
        for (Permission permission : childrenList) {
            //递归获取子菜单的id添加到列表中
            idList.add(permission.getId());
            findChildren(permission.getId(),idList);
        }
    }
    /**
    private List<Permission> childrenList(Long id,int level) {
        if (id == null) {
            return null;
        }
        //查询出来pid==id
        LambdaQueryWrapper<Permission> wrapper;
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid, id);
        List<Permission> currentMenus = baseMapper.selectList(wrapper);

        //再找当前目录的子目录
        for (Permission currentMenu : currentMenus) {
            List<Permission> children = childrenList(currentMenu.getId(),level+1);
            currentMenu.setLevel(level+1);
            currentMenu.setChildren(children);
        }
       return currentMenus;
    }
    **/
}
