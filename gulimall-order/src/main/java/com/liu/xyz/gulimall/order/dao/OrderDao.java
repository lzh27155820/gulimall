package com.liu.xyz.gulimall.order.dao;

import com.liu.xyz.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 23:54:53
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
