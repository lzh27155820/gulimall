package com.liu.xyz.gulimall.member.controller;

import com.liu.xyz.common.productUtils.BizCodeEnume;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.common.vo.MemberResponseVo;
import com.liu.xyz.gulimall.member.entity.MemberEntity;
import com.liu.xyz.gulimall.member.ex.PhoneException;
import com.liu.xyz.gulimall.member.ex.UsernameException;
import com.liu.xyz.gulimall.member.service.MemberService;
import com.liu.xyz.gulimall.member.vo.MemberUserLoginVo;
import com.liu.xyz.gulimall.member.vo.UserRegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-30 14:33:05
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    @GetMapping("gitee")
   public R giteeLogin(@RequestParam("url") String url){

        MemberEntity member = memberService.giteeLogin(url);

        MemberResponseVo memberRespon = new MemberResponseVo();
        BeanUtils.copyProperties(member,memberRespon);
        return R.ok().put("data",memberRespon);
    }

    @GetMapping("/giteeLogin")
    public R gitee(@RequestBody MemberResponseVo memberResponseVo){

      MemberEntity member = memberService.gitee(memberResponseVo);
        MemberResponseVo memberRespon = new MemberResponseVo();
      BeanUtils.copyProperties(member,memberRespon);
      return R.ok().put("data",memberRespon);
    }
    /**
     * 微信登入
     */
    @GetMapping("/weixinLogin")
    public R weixinLogin(@RequestParam("openid") String openid,
                         @RequestParam("accessToken") String accessToken) {

        MemberEntity member=memberService.weixinLogin(openid,accessToken);


        if(member==null){
            return R.error();
        }else {
            MemberResponseVo memberResponseVo = new MemberResponseVo();
            BeanUtils.copyProperties(member,memberResponseVo);
            return R.ok().put("data",memberResponseVo);
        }
    }

    /**
     * 登入
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberUserLoginVo vo){

      MemberEntity memberEntity= memberService.login(vo);

      if(memberEntity==null){
          return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getMsg());
      }
        return R.ok().put("data",memberEntity);
    }

    /**
     *  注册
     * @param userRegisterVo
     * @return
     */
    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo){

        try {
            memberService.register(userRegisterVo);
        } catch (PhoneException e) {
            return R.error().put("msg", BizCodeEnume.PHONE_EXIST_EXCEPTION);
        }catch (UsernameException e){
            return R.error().put("msg",BizCodeEnume.USER_EXIST_EXCEPTION);
        }
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
