package com.liu.xyz.gulimall.coupon.dao;

import com.liu.xyz.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-30 00:34:59
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
