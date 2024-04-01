package com.atguigu.ssyx.acl.mapper;

import com.atguigu.ssyx.model.acl.AdminRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRoleMapper extends BaseMapper<AdminRole> {
    void updateRoleIdsByAdminId(Long adminId, List<Long> roleIds);
}
