package com.liu.xyz.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liu.xyz.gulimall.product.valid.AddGroup;
import com.liu.xyz.gulimall.product.valid.ListValue;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	//@NotBlank(message = "修改是不能为空",groups = {UpdateGroup.class})
	@Null(message = "新增时只能为null",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空",groups = {AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotNull(message = "地址不能为空",groups = {AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */

	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */

	//@NotNull(message = "不能为空",groups = {AddGroup.class, UpdateStatus.class})
	@ListValue(value={0,1},groups = {AddGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */

	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "新增时不能为空",groups = {AddGroup.class})
	@Min(value = 0,message = "最小值为0",groups = {AddGroup.class})
	private Integer sort;

}
