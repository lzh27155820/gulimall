package com.liu.xyz.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.product.dao.CategoryDao;
import com.liu.xyz.gulimall.product.entity.CategoryEntity;
import com.liu.xyz.gulimall.product.service.CategoryBrandRelationService;
import com.liu.xyz.gulimall.product.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        List<CategoryEntity> categoryEntityList = baseMapper.selectList(new QueryWrapper<>());

        List<CategoryEntity> list = categoryEntityList.stream()
                .filter(meun -> meun.getParentCid().equals(0L))
                .map(meun -> searchChildren(meun, categoryEntityList))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public void removeByIdAll(Long[] catIds){
        //TODO 如果别的地方要用就不能删除
        List<Long> collect = Arrays.stream(catIds).collect(Collectors.toList());
        baseMapper.deleteBatchIds(collect);
    }

    @Override
    public Long[] getByIdCatelogPath(Long catelogId) {
        ArrayList<Long> longs = new ArrayList<>();
        get(catelogId,longs);

        Collections.reverse(longs);
        return longs.toArray(new Long[longs.size()]);
    }

    @Transactional
    @Override
    public void updateByIdDeatil(CategoryEntity category) {
        baseMapper.updateById(category);
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
        }
    }

    public List<Long> get(Long catelogId ,List<Long> longs){

        longs.add(catelogId);
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        if(categoryEntity.getParentCid()!=0){
            get(categoryEntity.getParentCid(),longs);
        }
        return longs;

    }


    public CategoryEntity searchChildren(CategoryEntity meun,List<CategoryEntity> all){

        List<CategoryEntity> list = all.stream()
                .filter(allmeun -> allmeun.getParentCid().equals(meun.getCatId()))
                .map(allmeun -> {
                    return searchChildren(allmeun, all);
                })
                .collect(Collectors.toList());

        meun.setChildren(list);
        return meun;
    }

//    @Override
//    public List<CategoryEntity> listWithTree() {
//
//        List<CategoryEntity> root = baseMapper.selectList(new QueryWrapper<>());
//
//        List<CategoryEntity> list = root.stream().filter(menu -> menu.getParentCid().equals(0L)).map(meun1 -> {
//                    meun1.setChildren(searchChildren(meun1, root));
//                    return meun1;
//                }).sorted((m1, m2) -> {
//                    return (m1.getSort() == null ? 0 : m1.getSort()) - (m2.getSort() == null ? 0 : m2.getSort());
//                })
//                .collect(Collectors.toList());
//
//
//        return list;
//    }
//
//
//    public List<CategoryEntity> searchChildren(CategoryEntity meun,List<CategoryEntity> all){
//
//        List<CategoryEntity> list = all.stream().filter(category1 -> {
//            return meun.getCatId().equals(category1.getParentCid());
//        }).map(category2 -> {
//            category2.setChildren(searchChildren(category2, all));
//            return category2;
//        }).sorted((m1, m2) -> {
//            return (m1.getSort() == null ? 0 : m1.getSort()) - (m2.getSort() == null ? 0 : m2.getSort());
//        }).collect(Collectors.toList());
//        return list;
//    }
}