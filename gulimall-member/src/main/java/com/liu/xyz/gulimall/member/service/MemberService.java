package com.liu.xyz.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.vo.MemberResponseVo;
import com.liu.xyz.gulimall.member.entity.MemberEntity;
import com.liu.xyz.gulimall.member.ex.PhoneException;
import com.liu.xyz.gulimall.member.ex.UsernameException;
import com.liu.xyz.gulimall.member.vo.MemberUserLoginVo;
import com.liu.xyz.gulimall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-30 14:33:05
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     * @param userRegisterVo
     */
    void register(UserRegisterVo userRegisterVo);
    /**
     * 检测手机号是否唯一
     */
    void checkPhoneUique(String phone)throws PhoneException;

    /**
     *  检测用户名是否唯一
     */
    void checkUserNameUique(String userName)throws UsernameException;

    /**
     *  登入
     * @param vo
     * @return
     */
    MemberEntity login(MemberUserLoginVo vo);

    /**
     * 查询用户是微信登入
     * @param openid
     * @return
     */
    MemberEntity weixinLogin(String openid,String accessToken);

    /**
     * gitee 用户登入
     * @param memberResponseVo
     * @return
     */
    MemberEntity gitee(MemberResponseVo memberResponseVo);

    MemberEntity giteeLogin(String url);
}

