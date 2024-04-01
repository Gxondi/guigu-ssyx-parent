package com.atguigu.ssyx.acl.helper;

import com.atguigu.ssyx.acl.mapper.PermissionMapper;
import com.atguigu.ssyx.model.acl.Permission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限数据菜单帮助类
 */
public class PermissionHelper {
    @Autowired
    private PermissionMapper baseMapper;

    public static List<Permission> build(List<Permission> allPermissionList) {
        //1.获取一级目录
        List<Permission> trees = new ArrayList<>();
        //遍历每个目录
        for (Permission permission : allPermissionList) {
            //pid==0的是一级目录
            if (permission.getPid() == 0) {
                //设置一级目录的级别
                permission.setLevel(1);
                //将一级目录添加到trees中,递归获取子级目录
                trees.add(findChildren(permission, allPermissionList));

            }
        }
        return trees;
    }

    /**
     * 递归获取子级目录
     *
     * @param currentTreeNode 当前菜单
     * @param allTreeNodes    总菜单
     * @return
     */
    public static Permission findChildren(Permission currentTreeNode, List<Permission> allTreeNodes) {
        currentTreeNode.setChildren(new ArrayList<Permission>());
        //遍历总菜单
        for (Permission treeNode : allTreeNodes) {
            //如果总菜单中有某个pid等于当前菜单的id,则将总菜单中的这个菜单添加到当前菜单的子菜单中
            if (currentTreeNode.getId().longValue() == treeNode.getPid().longValue()) {
                int level = currentTreeNode.getLevel() + 1;
                treeNode.setLevel(level);
                //如果当前菜单的子菜单为空,则创建一个新的子菜单
                if (currentTreeNode.getChildren() == null) {
                    currentTreeNode.setChildren(new ArrayList<>());
                }
                //获取当前菜单的子菜单
                List<Permission> children = currentTreeNode.getChildren();
                //继续递归获取子菜单的子菜单
                children.add(findChildren(treeNode, allTreeNodes));
            }
        }
        return currentTreeNode;
    }
}
