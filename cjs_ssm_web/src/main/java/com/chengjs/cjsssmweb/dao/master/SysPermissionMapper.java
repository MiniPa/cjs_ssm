package com.chengjs.cjsssmweb.dao.master;

import com.chengjs.cjsssmweb.pojo.SysPermission;

public interface SysPermissionMapper {
  /**
   * This method was generated by MyBatis Generator.
   * This method corresponds to the database table sys_permission
   *
   * @mbggenerated
   */
  int deleteByPrimaryKey(String perId);

  /**
   * This method was generated by MyBatis Generator.
   * This method corresponds to the database table sys_permission
   *
   * @mbggenerated
   */
  int insert(SysPermission record);

  /**
   * This method was generated by MyBatis Generator.
   * This method corresponds to the database table sys_permission
   *
   * @mbggenerated
   */
  int insertSelective(SysPermission record);

  /**
   * This method was generated by MyBatis Generator.
   * This method corresponds to the database table sys_permission
   *
   * @mbggenerated
   */
  SysPermission selectByPrimaryKey(String perId);

  /**
   * This method was generated by MyBatis Generator.
   * This method corresponds to the database table sys_permission
   *
   * @mbggenerated
   */
  int updateByPrimaryKeySelective(SysPermission record);

  /**
   * This method was generated by MyBatis Generator.
   * This method corresponds to the database table sys_permission
   *
   * @mbggenerated
   */
  int updateByPrimaryKey(SysPermission record);
}