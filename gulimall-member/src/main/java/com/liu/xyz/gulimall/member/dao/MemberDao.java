package com.liu.xyz.gulimall.member.dao;

import com.liu.xyz.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-30 14:33:05
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
