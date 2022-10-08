package com.liu.xyz.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.ware.dao.PurchaseDetailDao;
import com.liu.xyz.gulimall.ware.entity.PurchaseDetailEntity;
import com.liu.xyz.gulimall.ware.service.PurchaseDetailService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * key:
         * status:
         * wareId:
         */

        String key =(String) params.get("key");

        String status =(String) params.get("status");
        String wareId =(String) params.get("wareId");

        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(key)){
            wrapper.and(obj->{
               obj.eq("id",key).or().eq("sku_id",key);
            });
        }
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
            wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDateilByPurchaseId(Long id) {

        List<PurchaseDetailEntity> purchase_id = baseMapper.selectList(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));

        return purchase_id;
    }

}