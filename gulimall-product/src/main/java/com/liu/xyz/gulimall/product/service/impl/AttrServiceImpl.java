package com.liu.xyz.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.productUtils.ProductConstant;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.product.dao.AttrDao;
import com.liu.xyz.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.liu.xyz.gulimall.product.entity.AttrEntity;
import com.liu.xyz.gulimall.product.entity.AttrGroupEntity;
import com.liu.xyz.gulimall.product.entity.CategoryEntity;
import com.liu.xyz.gulimall.product.service.AttrAttrgroupRelationService;
import com.liu.xyz.gulimall.product.service.AttrGroupService;
import com.liu.xyz.gulimall.product.service.AttrService;
import com.liu.xyz.gulimall.product.service.CategoryService;
import com.liu.xyz.gulimall.product.vo.AttrRespVO;
import com.liu.xyz.gulimall.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService relationService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrGroupService attrGroupService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();

        BeanUtils.copyProperties(attr,attrEntity);

        baseMapper.insert(attrEntity);
        //保村关联关系

        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()&&
        attr.getAttrGroupId()!=null) {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();

            entity.setAttrGroupId(attr.getAttrGroupId());


            entity.setAttrId(attrEntity.getAttrId());
            relationService.save(entity);
        }

    }

    @Transactional
    @Override
    public PageUtils select(Map<String, Object> params, Long catgoryId,String attrType) {
        //根据字段查询key
        String key =(String) params.get("key");
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type",
                        "base".equalsIgnoreCase(attrType)?
                                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                                :ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if(catgoryId!=0){
            wrapper.eq("catelog_id",catgoryId);
        }
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);

        /**
         * 返回 AttrRespVO
         *      封装两个字段
         *      * "catelogName": "手机/数码/手机", //所属分类名字
         *      * 			"groupName": "主体", //所属分组名字
         */
        List<AttrEntity> list = page.getRecords();



        List<AttrRespVO> voList = list.stream().map((attrEntity -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, attrRespVO);

            AttrAttrgroupRelationEntity entity = relationService.getOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrEntity.getAttrId()));
            if (entity != null&&entity.getAttrGroupId()!=null) {

                if("base".equalsIgnoreCase(attrType)&&entity.getAttrGroupId()!=null){
                    AttrGroupEntity attrGroup = attrGroupService.getOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id", entity.getAttrGroupId()));

                    attrRespVO.setGroupName(attrGroup.getAttrGroupName());
                }


            }
            CategoryEntity category = categoryService.getById(attrEntity.getCatelogId());
            if (category != null) {

                attrRespVO.setCatelogName(category.getName());
            }
            return attrRespVO;
        })).collect(Collectors.toList());


        PageUtils pageUtils = new PageUtils(page);

        pageUtils.setList(voList);
        return pageUtils;
    }
    @Transactional
    @Override
    public AttrRespVO getByIdAttrRespVO(Long attrId) {
        AttrRespVO attrRespVO = new AttrRespVO();
        AttrEntity attrEntity = baseMapper.selectById(attrId);

        BeanUtils.copyProperties(attrEntity,attrRespVO);
        //设置基本属性
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity entity = relationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrEntity.getAttrId()));
            if (entity != null) {

                AttrGroupEntity group = attrGroupService.getById(entity.getAttrGroupId());
                if (group != null) {
                    attrRespVO.setAttrGroupId(group.getAttrGroupId());
                    attrRespVO.setGroupName(group.getAttrGroupName());
                }
            }
        }

        Long[] catelogPath = categoryService.getByIdCatelogPath(attrEntity.getCatelogId());
        if(catelogPath!=null){
            attrRespVO.setCatelogPath(catelogPath);
        }
        CategoryEntity byId = categoryService.getById(attrEntity.getCatelogId());
        if(byId!=null){
            attrRespVO.setCatelogName(byId.getName());
        }
        return attrRespVO;
    }

    @Override
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();

        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrId = relationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attr.getAttrId()));
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrId(attr.getAttrId());
            entity.setAttrGroupId(attr.getAttrGroupId());
            if (attrId == null) {
                relationService.save(entity);
            } else {
                relationService.update(entity, new UpdateWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attr.getAttrId()));
            }
        }
    }

}