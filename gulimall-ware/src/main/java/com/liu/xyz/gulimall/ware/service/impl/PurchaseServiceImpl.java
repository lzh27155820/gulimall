package com.liu.xyz.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.productUtils.WareConstant;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.ware.dao.PurchaseDao;
import com.liu.xyz.gulimall.ware.entity.PurchaseDetailEntity;
import com.liu.xyz.gulimall.ware.entity.PurchaseEntity;
import com.liu.xyz.gulimall.ware.service.PurchaseDetailService;
import com.liu.xyz.gulimall.ware.service.PurchaseService;
import com.liu.xyz.gulimall.ware.service.WareSkuService;
import com.liu.xyz.gulimall.ware.vo.MergeVo;
import com.liu.xyz.gulimall.ware.vo.PurchaseDoneVo;
import com.liu.xyz.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService detailService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils unreceiveQueryPage(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {

        Long purchaseId = mergeVo.getPurchaseId();
        if(purchaseId==null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);

         purchaseId= purchaseEntity.getId();

        }
        //TODO 确认采购单状态是0,1才可以合并

//        PurchaseEntity byId = this.getById(purchaseId);
//        if(byId.getStatus()==WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()||
//        byId.getStatus()==WareConstant.PurchaseStatusEnum.CREATED.getCode()){
            List<Long> items = mergeVo.getItems();
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(obj -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(obj);
                detailEntity.setPurchaseId(finalPurchaseId);
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            detailService.updateBatchById(collect);

            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
//        }else {
//            throw  new RuntimeException("采购单已取");
//        }



    }

    @Override
    public void receivedPurchase(List<Long> ids) {
        //1.确认当前采购单是新建或者分配
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item->{
            //只能放行已分配 或新建
            if(item.getStatus()==WareConstant.PurchaseStatusEnum.CREATED.getCode()||
            item.getStatus()==WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()){
                return true;}
            return false;
        }).map(item->{
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        //改编采购单的状态
        this.updateBatchById(collect);

        //3.改变采购项的状态
        collect.forEach(item->{
           List<PurchaseDetailEntity> entiti=detailService.listDateilByPurchaseId(item.getId());
            List<PurchaseDetailEntity> list = entiti.stream().map(ent -> {
                PurchaseDetailEntity entity = new PurchaseDetailEntity();
                entity.setId(ent.getId());
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity;
            }).collect(Collectors.toList());
            detailService.updateBatchById(list);
        });

    }

    @Autowired
    private WareSkuService wareSkuService;

    @Transactional
    @Override
    public void dong(PurchaseDoneVo purchaseDoneVo) {
        //1. 改变采购单的状态
        Long id = purchaseDoneVo.getId();

        //2.改变采购项的状态
        Boolean flag=true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        ArrayList<PurchaseDetailEntity> entities = new ArrayList<>();
        for (PurchaseItemDoneVo e:items){
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if(e.getStatus()==WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag=false;
                detailEntity.setStatus(e.getStatus());
            }else {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                PurchaseDetailEntity byId = detailService.getById(e.getItemId());
                wareSkuService.addStock(byId.getSkuId(),byId.getWareId(),byId.getSkuNum());
            }
            detailEntity.setId(e.getItemId());
            entities.add(detailEntity);
        }
        //
        detailService.updateBatchById(entities);

        //1、改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode():WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}