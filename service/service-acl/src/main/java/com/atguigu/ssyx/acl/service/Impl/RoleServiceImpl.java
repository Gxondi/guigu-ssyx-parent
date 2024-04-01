package com.atguigu.ssyx.acl.service.Impl;

import com.atguigu.ssyx.acl.mapper.AdminRoleMapper;
import com.atguigu.ssyx.acl.mapper.RoleMapper;
import com.atguigu.ssyx.acl.service.RoleService;
import com.atguigu.ssyx.model.acl.AdminRole;
import com.atguigu.ssyx.model.acl.Role;
import com.atguigu.ssyx.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
   @Autowired
   private AdminRoleMapper adminRoleMapper;
    @Override
    public IPage<Role> selectRolePage(Page pageParam, RoleQueryVo roleQueryVo) {
        String roleName = roleQueryVo.getRoleName();
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        //不为空模糊查询角色名称
        if(!StringUtils.isEmpty(roleName)){
            return baseMapper.selectPage(pageParam, wrapper.like(Role::getRoleName, roleName));
        }
        //为空查询全部
        return baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Role getRoleById(Long id) {
        //LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        //LambdaQueryWrapper<Role> eq = wrapper.eq(Role::getId, id);
        Role role = baseMapper.selectById(id);
        return role;
    }

    @Override
    public Map<String, Object> getRolesByAdminId(Long adminId) {
        //1.查询所有角色
        List<Role> allRolesList = baseMapper.selectList(null);
        Map<String,Object> result = new HashMap<>();
        //2.根据id查询用户已有角色
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper();
        wrapper.eq(AdminRole::getAdminId,adminId);
        List<AdminRole> adminRoles = adminRoleMapper.selectList(wrapper);
        List<Long> roleIdList = adminRoles.stream().map(adminRole -> {
            return adminRole.getRoleId();
        }).collect(Collectors.toList());

        List<Role> assignRoleList = new ArrayList<>();
        for (Role role : allRolesList) {
            if (roleIdList.contains(role.getId())){
                assignRoleList.add(role);
            }
        }

        result.put("allRolesList",allRolesList);
        result.put("assignRoles",assignRoleList);
        return result;
    }

    @Override
    public void saveAdminRole(Long adminId, Long[] roleIds) {
        //1.删除原有角色
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId,adminId);
        int delete = adminRoleMapper.delete(wrapper);
        System.out.println("删除了"+delete+"条记录");
        //2.保存新的角色
        for (Long roleId : roleIds) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            adminRoleMapper.insert(adminRole);
        }
    }

}
