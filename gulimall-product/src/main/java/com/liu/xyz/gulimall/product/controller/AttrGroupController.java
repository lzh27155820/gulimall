package com.liu.xyz.gulimall.product.controller;

import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.gulimall.product.entity.AttrEntity;
import com.liu.xyz.gulimall.product.entity.AttrGroupEntity;
import com.liu.xyz.gulimall.product.service.AttrGroupService;
import com.liu.xyz.gulimall.product.service.CategoryService;
import com.liu.xyz.gulimall.product.vo.AttrAndGroupID;
import com.liu.xyz.gulimall.product.vo.AttrGroupWithAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 属性分组
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    /**
     * http://localhost:88/api/product/attrgroup/225/withattr?t=1665035579458
     */
    @RequestMapping("/{categoryId}/withattr")
    public R attrgroupWithattr(@PathVariable("categoryId") Long categoryId){

       List<AttrGroupWithAttrs> list=attrGroupService.getAttrGroupWithAttrByCategoryId(categoryId);

        return R.ok().put("data",list);
    }
    @RequestMapping("/attr/relation")
    public R attrRelation(@RequestBody List<AttrAndGroupID> andGroupIDS){

        List<AttrGroupEntity> collect = andGroupIDS.stream().map((obj) -> {
            AttrGroupEntity attrGroup = new AttrGroupEntity();
            attrGroup.setAttrGroupId(obj.getAttrGroupId());
            attrGroup.setCatelogId(obj.getAttrId());
            return attrGroup;
        }).collect(Collectors.toList());
        attrGroupService.saveBatch(collect);

        return R.ok();
    }

    /**
     *
     * @return
     */
    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R noattrRelation(@RequestParam Map<String, Object> params,
                            @PathVariable("attrGroupId") Long attrGroupId){

            PageUtils pageUtils=attrGroupService.getNoattrRelation(params,attrGroupId);

            return R.ok().put("page",pageUtils);
    }


    @RequestMapping("/attr/relation/delete")
    public R attrRelationDelete(@RequestBody AttrAndGroupID[] andGroupIDS){

        attrGroupService.removeId(andGroupIDS);

        return R.ok();
    }
    /**
     *  通过id 查询与 规格参数 关联
     * @param attrgroupId
     * @return
     */
    @RequestMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){

       List<AttrEntity> attrEntitie =attrGroupService.selectListByAttrgroupId(attrgroupId);

       return R.ok().put("data",attrEntitie);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId ){
       // PageUtils page = attrGroupService.queryPage(params);

        PageUtils page =attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        attrGroup.setCatelogPath(categoryService.getByIdCatelogPath(attrGroup.getCatelogId()));
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
