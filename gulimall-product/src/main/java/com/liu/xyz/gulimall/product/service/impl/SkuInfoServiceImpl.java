package com.liu.xyz.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.product.dao.SkuInfoDao;
import com.liu.xyz.gulimall.product.entity.SkuImagesEntity;
import com.liu.xyz.gulimall.product.entity.SkuInfoEntity;
import com.liu.xyz.gulimall.product.entity.SpuInfoDescEntity;
import com.liu.xyz.gulimall.product.service.*;
import com.liu.xyz.gulimall.product.web.vo.SkuItemSaleAttrVo;
import com.liu.xyz.gulimall.product.web.vo.SkuItemVo;
import com.liu.xyz.gulimall.product.web.vo.SpuItemAttrGroupVo;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String)params.get("key");
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
               obj.eq("sku_id",key).or().like("sku_name",key);
            });
        }

        String catelogId =(String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }
        String brandId =(String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String min =(String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            wrapper.ge("price",min);
        }
        String max =(String) params.get("max");

        if(!StringUtils.isEmpty(max)){

            BigDecimal bigDecimal = new BigDecimal(max);
            if(bigDecimal.compareTo(new BigDecimal("0"))==1){
                wrapper.le("price",max);
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        baseMapper.insert(skuInfoEntity);
    }

    @Autowired
    ThreadPoolExecutor executor;
    @SneakyThrows
    @Override
    public SkuItemVo searchToskuId(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        //1、sku基本信息的获取  pms_sku_info
//        SkuInfoEntity info = baseMapper.selectById(skuId);
//        skuItemVo.setInfo(info);

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity info = baseMapper.selectById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);
        //3、获取spu的销售属性组合
        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((result -> {
            List<SkuItemSaleAttrVo> list = skuSaleAttrValueService.listSkuSaleAttrValue(result.getSpuId());
            skuItemVo.setSaleAttr(list);
        }), executor);
        //4、获取spu的介绍
        CompletableFuture<Void> SpuDescFuture = infoFuture.thenAcceptAsync((result) -> {
            SpuInfoDescEntity descServiceOne =
                    spuInfoDescService.getOne(new QueryWrapper<SpuInfoDescEntity>().eq("spu_id", result.getSpuId()));
            skuItemVo.setDesc(descServiceOne);
        }, executor);
        //5、获取spu的规格参数信息
        CompletableFuture<Void> SpuBaseAttr = infoFuture.thenAcceptAsync((result) -> {
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.
                    getAttrGroupWithAttrsBySpuId(result.getSpuId(), result.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);
        //2、sku的图片信息    pms_sku_images
        CompletableFuture<Void> SkuImgFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> imagesEntityList = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            skuItemVo.setImages(imagesEntityList);
        }, executor);

        CompletableFuture.allOf(saleAttrFuture,SpuDescFuture,SpuBaseAttr,SkuImgFuture).get();



        //2、sku的图片信息    pms_sku_images
//        List<SkuImagesEntity> imagesEntityList = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
//        skuItemVo.setImages(imagesEntityList);

        //3、获取spu的销售属性组合
//        List<SkuItemSaleAttrVo>  list=skuSaleAttrValueService.listSkuSaleAttrValue(info.getSpuId());
//        skuItemVo.setSaleAttr(list);

        //4、获取spu的介绍
//        SpuInfoDescEntity descServiceOne =
//                spuInfoDescService.getOne(new QueryWrapper<SpuInfoDescEntity>().eq("spu_id", info.getSpuId()));
//        skuItemVo.setDesc(descServiceOne);
        //5、获取spu的规格参数信息
//        List<SpuItemAttrGroupVo> attrGroupVos =attrGroupService.getAttrGroupWithAttrsBySpuId(info.getSpuId(),info.getCatalogId());
//        skuItemVo.setGroupAttrs(attrGroupVos);
        //6、秒杀商品的优惠信息


        return skuItemVo;
    }

}