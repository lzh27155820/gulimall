package com.liu.xyz.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.productUtils.ProductConstant;
import com.liu.xyz.common.to.SkuHasStockVo;
import com.liu.xyz.common.to.SkuReductionTo;
import com.liu.xyz.common.to.SpuBoundsTo;
import com.liu.xyz.common.to.es.SkuESModel;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.gulimall.product.dao.SpuInfoDao;
import com.liu.xyz.gulimall.product.entity.*;
import com.liu.xyz.gulimall.product.feign.CouponFeignService;
import com.liu.xyz.gulimall.product.feign.SearchFeignSerive;
import com.liu.xyz.gulimall.product.feign.WareFeignService;
import com.liu.xyz.gulimall.product.service.*;
import com.liu.xyz.gulimall.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService imagesService;

    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService ;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String key =(String) params.get("key");
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
               obj.eq("id",key).or().like("spu_name",key);
            });
        }
        String status =(String) params.get("status") ;
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId =(String)  params.get("brandId");
        String catelogId =(String) params.get("catelogId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }
    //TODO 还有高级部分
    @Transactional
    @Override
    public void saveSpuSaveVo(SpuSaveVo spuInfo) {
        //1.保持基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity=new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
        //2.保存spu的描述图片  pms_spu_info_desc
        List<String> decript = spuInfo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveDesc(descEntity);

        //3.保存spu的图片集 pms_spu_image
        List<String> images = spuInfo.getImages();
        imagesService.saveImage(spuInfoEntity.getId(),images);

        //4.保存spu的参数规格 pms_spu_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProducAttr(collect);
        //保存spu积分 gulimall-sms-》sms_spu_bounds
        //5 保存当前spu对应的所有sku信息
        Bounds bounds = spuInfo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds,spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBouds(spuBoundsTo);



        //5.1 sku的基本信息 pms_sku_images
        List<Skus> skus = spuInfo.getSkus();
        if(skus!=null&&skus.size()>0){
            skus.forEach(itme->{

                String defaultImg="";

                for (Images e:itme.getImages()){
                    if(e.getDefaultImg()==1){
                        defaultImg=e.getImgUrl();
                    }
                }
                //5.1）、sku的基本信息；pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(itme,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfo.getBrandId());
                skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
                skuInfoEntity.setSaleCount(0l);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();
                //5.2 sku的基本信息 pms_sku_images
                List<SkuImagesEntity> skuImagesEntityList = itme.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();

                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());

                skuImagesService.saveBatch(skuImagesEntityList);
                //5.3 sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attr = itme.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attr.stream().map(att -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(att, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
               //5.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(itme,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount()>0||
                        skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
                   // skuReductionTo.setMemberPrice(itme.getMemberPrice());
                    List<MemberPrice> memberPrice = itme.getMemberPrice();

                    List<com.liu.xyz.common.to.MemberPrice> list = new
                            ArrayList<>();
                    for (MemberPrice e:memberPrice ){
                        com.liu.xyz.common.to.MemberPrice memberPrice1 = new com.liu.xyz.common.to.MemberPrice();
                        memberPrice1.setPrice(e.getPrice());
                        memberPrice1.setName(e.getName());
                        memberPrice1.setId(e.getId());
                        list.add(memberPrice1);
                    }
                    skuReductionTo.setMemberPrice(list);


                    couponFeignService.saveSkuRedution(skuReductionTo);
                }


            });
        }
        //5.2
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {

        baseMapper.insert(spuInfoEntity);
    }

    @Autowired
    private SearchFeignSerive searchFeignSerive;
    @Override
    public void up(Long spuId) {

        List<SkuESModel> skuESModelList = new ArrayList<>();
        //4. Todo 查询 所有规格属性 attrsList
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.baseAttrListforspu(spuId);

        List<Long> attrIds = attrValueEntities.stream().map(obj -> {
                    return obj.getAttrId();
                }
        ).collect(Collectors.toList());



      List<Long> searchAttrIds =attrService.getSearchAttrIds(attrIds);
        HashSet<Long> idset = new HashSet<>(searchAttrIds);

        ArrayList<SkuESModel.Attrs> attrs = new ArrayList<>();

        List<SkuESModel.Attrs> list = attrValueEntities.stream().filter(obj -> {

            return idset.contains(obj.getAttrId());
        }).map(obj -> {
            SkuESModel.Attrs attrs1 = new SkuESModel.Attrs();
            BeanUtils.copyProperties(obj, attrs1);
            return attrs1;
        }).collect(Collectors.toList());



        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        //1.TODO 发送远程调用 查看 库存 hastStock hotScore
        Map<Long, Boolean> map =new HashMap<>();
        try {
            List<Long> skuIds = skuInfoEntityList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            R<List<SkuHasStockVo>> r = wareFeignService.hastStock(skuIds);
            List<SkuHasStockVo>  stockVoList=(List<SkuHasStockVo>) r.get("data");
           // System.out.println(stockVoList);
            for(Object e:stockVoList){
                String s = JSON.toJSONString(e);
                SkuHasStockVo vo = JSONObject.parseObject(s, SkuHasStockVo.class);
                map.put(vo.getSkuId(),vo.getHastStock());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        Map<Long, Boolean> finalMap = map;
        List<SkuESModel> collect = skuInfoEntityList.stream().map(skuInfo -> {
            SkuESModel esModel = new SkuESModel();

            BeanUtils.copyProperties(skuInfo, esModel);
            //单独处理 skuImg skuPrice  brandName brandImg catalogName
            esModel.setSkuPrice(skuInfo.getPrice());
            esModel.setSkuImg(skuInfo.getSkuDefaultImg());
            if(finalMap ==null){
                esModel.setHastStock(true);
            }else {
                esModel.setHastStock(finalMap.get(skuInfo.getSkuId()));
            }

            //2.TODO 热度评分

            //3.TODO 查询品牌和分类的信息
            BrandEntity brand = brandService.getById(skuInfo.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(skuInfo.getCatalogId());
            esModel.setCatalogId(category.getCatId());
            esModel.setCatalogName(category.getName());
            //
            esModel.setAttrs(list);
            return esModel;
        }).collect(Collectors.toList());
        //保存到es中
        R r = searchFeignSerive.get(collect);

        if(r.getCode()==0){

            baseMapper.updateStatus(spuId, ProductConstant.StatusEnum.UP_SPU.getCode());
        }else {
            //TODO 重复调用问题
        }

    }


}