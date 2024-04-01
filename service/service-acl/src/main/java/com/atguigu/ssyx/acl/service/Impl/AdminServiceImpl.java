package com.atguigu.ssyx.acl.service.Impl;

import com.atguigu.ssyx.acl.mapper.AdminMapper;
import com.atguigu.ssyx.acl.mapper.AdminRoleMapper;
import com.atguigu.ssyx.acl.mapper.RoleMapper;
import com.atguigu.ssyx.acl.service.AdminRoleService;
import com.atguigu.ssyx.acl.service.AdminService;
import com.atguigu.ssyx.acl.service.RoleService;
import com.atguigu.ssyx.model.acl.Admin;
import com.atguigu.ssyx.model.acl.AdminRole;
import com.atguigu.ssyx.model.acl.Role;
import com.atguigu.ssyx.vo.acl.AdminQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Override
    public IPage<Admin> selectAdminPage(Page pageParam, AdminQueryVo adminQueryVo) {
        String username = adminQueryVo.getUsername();
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper();
        Page page = baseMapper.selectPage(pageParam, wrapper.like(Admin::getUsername, username));
        return page;
    }

    @Override
    public List<Role> getRoles(Long adminId) {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper();
        wrapper.eq(AdminRole::getAdminId, adminId);
        List<AdminRole> adminRoles = adminRoleMapper.selectList(wrapper);
        List<Long> collect = adminRoles.stream().map(adminRole -> {
            return adminRole.getRoleId();
        }).collect(Collectors.toList());
        System.out.println(collect);
        List<Role> roles = roleMapper.selectBatchIds(collect);
        return roles;
    }


}
