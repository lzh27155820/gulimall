package com.liu.xyz.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.ware.dao.WareInfoDao;
import com.liu.xyz.gulimall.ware.entity.WareInfoEntity;
import com.liu.xyz.gulimall.ware.service.WareInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {


        QueryWrapper<WareInfoEntity> wr = new QueryWrapper<>();
        String key =(String) params.get("key");

        if(!StringUtils.isEmpty(key)){
            wr.eq("id",key).or().like("name",key)
                    .or().like("address",key).like("areacode",key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wr
        );

        return new PageUtils(page);
    }

}