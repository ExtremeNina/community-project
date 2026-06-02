package com.example.onlyone.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlyone.Entity.RoleAndPermission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联 Mapper
 *
 * 修复说明：BaseMapper 泛型参数已从 RoleAndPermissionMapper（自身）修正为 RoleAndPermission（实体类）。
 * 原 Bug：BaseMapper<RoleAndPermissionMapper> 导致 MyBatis Plus 无法正确注册该 Mapper，
 * queryPermission 方法调用失败，所有 Permission 查询返回空。
 */
public interface RoleAndPermissionMapper extends BaseMapper<RoleAndPermission> {

    @Select("select  * from role_and_permission where role_id = #{roleId}")
    List<RoleAndPermission> queryPermission(Long roleId);
}
