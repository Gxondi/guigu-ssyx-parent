package com.atguigu.ssyx.acl.service;

import com.atguigu.ssyx.model.acl.Role;
import com.atguigu.ssyx.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface RoleService extends IService<Role> {
    IPage<Role> selectRolePage(Page pageParam, RoleQueryVo roleQueryVo);

    Role getRoleById(Long id);

    Map<String, Object> getRolesByAdminId(Long adminId);

    void saveAdminRole(Long adminId, Long[] roleId);
}
