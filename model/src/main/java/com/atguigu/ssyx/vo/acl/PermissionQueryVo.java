package com.atguigu.ssyx.vo.acl;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "子目录添加实体类")
public class PermissionQueryVo {
    @ApiModelProperty(value = "上级部门parentId")
    private Long parentId;
    @ApiModelProperty(value = "部门名称")
    private String name;
    @ApiModelProperty(value = "部门编码")
    private String code;
    @ApiModelProperty(value = "部门类型(1:菜单,2:按钮)")
    private Integer type;
}
