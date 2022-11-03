package com.liu.xyz.feign;

import com.liu.xyz.common.utils.R;
import com.liu.xyz.common.vo.MemberResponseVo;
import com.liu.xyz.vo.UserLoginVo;
import com.liu.xyz.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * create liu 2022-10-30
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @GetMapping("/member/member/weixinLogin")
   public R weixinLogin(@RequestParam("openid") String openid,@RequestParam("accessToken") String accessToken);

    @GetMapping("/member/member/giteeLogin")
    R gitee(@RequestBody MemberResponseVo memberResponseVo);
    @GetMapping("/member/member/gitee")
    R giteeLogin(@RequestParam("url") String url);
}
