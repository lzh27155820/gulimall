package com.liu.xyz.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.productUtils.ProductConstant;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.liu.xyz.gulimall.product.dao.AttrDao;
import com.liu.xyz.gulimall.product.dao.AttrGroupDao;
import com.liu.xyz.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.liu.xyz.gulimall.product.entity.AttrEntity;
import com.liu.xyz.gulimall.product.entity.AttrGroupEntity;
import com.liu.xyz.gulimall.product.service.AttrGroupService;
import com.liu.xyz.gulimall.product.service.CategoryBrandRelationService;
import com.liu.xyz.gulimall.product.vo.AttrAndGroupID;
import com.liu.xyz.gulimall.product.vo.AttrGroupWithAttrs;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create liu 2022-10-05
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrAttrgroupRelationDao relationService;
    @Autowired
    private AttrDao attrDao;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        //如果点击的属性分类是零，返回分页全部，否则带字段查询
        if(catelogId==0){
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
            String key=(String) params.get("key");

            if(!StringUtils.isEmpty(key)){
                wrapper.eq("attr_group_id",key).or().like("attr_group_name",key);
                IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                        wrapper);
                return new  PageUtils(page);
            }
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<>());
            return new  PageUtils(page);
        }else {

            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId);

            String key=(String) params.get("key");
            if(!StringUtils.isEmpty(key)){
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }

            return new PageUtils(
                    this.page(new Query<AttrGroupEntity>().getPage(params),wrapper)
            );
        }

    }

    /**
     * 通过分组 id 查询 商品属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> selectListByAttrgroupId(Long attrgroupId) {


        List<AttrAttrgroupRelationEntity> groupId = relationService.selectList(new
                QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

        List<Long> attrIds = groupId.stream().map((obj) -> {
            return obj.getAttrId();
        }).collect(Collectors.toList());

        if(attrIds==null||attrIds.size()<=0){
            return null;
        }
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);

        return attrEntities;
    }

    @Override
    public void removeId(AttrAndGroupID[] andGroupIDS) {

        List<AttrAttrgroupRelationEntity> list = Arrays.stream(andGroupIDS).map((obj) -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(obj.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(obj.getAttrGroupId());
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());

        relationService.deleteList(list);
    }

    @Override
    public PageUtils getNoattrRelation(Map<String, Object> params, Long attrGroupId) {

        //查出当前属性分组
        AttrGroupEntity attrGroup = baseMapper.selectById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        //查询分组的所有的商品分类分组id
        List<AttrGroupEntity> entities = baseMapper.selectList(new
                QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));


        List<Long> AttrGroupIdList = entities.stream().map((obj) -> {
            return obj.getAttrGroupId();
        }).collect(Collectors.toList());

        //根据分组id 把商品属性全部查询到
        List<AttrAttrgroupRelationEntity> relationEntities = relationService.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", AttrGroupIdList));

        List<Long> collect = relationEntities.stream().map((obj) -> {
            return obj.getAttrId();
        }).collect(Collectors.toList());
        //确认类型 和 catelogId
        QueryWrapper<AttrEntity> wrapper =
                new QueryWrapper<AttrEntity>().eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())
                        .eq("catelog_id",catelogId);
        if(collect!=null&&collect.size()>0){
//            wrapper.and((obj)->{
//               obj.notIn("attr_id",collect);
//            });
            wrapper.notIn("attr_id",collect);
        }
        String key =(String) params.get("key");

        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_id",key).or().eq("attr_name",key);
            });
        }


        IPage<AttrEntity> page = attrDao.selectPage(new Query<AttrEntity>().getPage(params), wrapper);

        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    @Override
    public List<AttrGroupWithAttrs> getAttrGroupWithAttrByCategoryId(Long categoryId) {
        //查询所有分组信息
        List<AttrGroupEntity> entities = baseMapper.selectList(new
                QueryWrapper<AttrGroupEntity>().eq("catelog_id", categoryId));
        //
        List<AttrGroupWithAttrs> collect = entities.stream().map(obj -> {

            AttrGroupWithAttrs attrGroupWithAttrs = new AttrGroupWithAttrs();
            BeanUtils.copyProperties(obj, attrGroupWithAttrs);

            Long attrGroupId = obj.getAttrGroupId();
            List<AttrEntity> entityList = this.selectListByAttrgroupId(attrGroupId);
            attrGroupWithAttrs.setAttrs(entityList);


            return attrGroupWithAttrs;

        }).collect(Collectors.toList());

        return collect;
    }
}
