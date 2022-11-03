package com.liu.xyz.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.gson.Gson;
import com.liu.xyz.common.productUtils.AuthServerConstant;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.common.vo.MemberResponseVo;
import com.liu.xyz.config.ConstantPropertiesUtil;
import com.liu.xyz.config.HttpClientUtils;
import com.liu.xyz.feign.MemberFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * create liu 2022-10-31
 */
@Slf4j
@Controller
@RequestMapping(value = "/api/ucenter/wx")
public class Wx {

    @Autowired
    private MemberFeignService memberFeignService;




    @GetMapping("callback")
    public String callback(@RequestParam("code") String code, @RequestParam("state") String state,HttpSession session){
        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl =
                "https://api.weixin.qq.com/sns/oauth2/access_token" +"?appid=%s" +
                        "&secret=%s" +
                        "&code=%s" +
                        "&grant_type=authorization_code";
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        String result = null;
        try {
            //获取access_token 和 openid
            result = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accessToken=============" + result);
        } catch (Exception e) {
           // throw new GgktExceptionHander( "获取access_token失败",20001);
        }
        Gson gson = new Gson();

        HashMap hashMap = gson.fromJson(result, HashMap.class);

        String accessToken = (String) hashMap.get("access_token");
        String openid = (String)hashMap.get("openid");

        R r=memberFeignService.weixinLogin(openid,accessToken);
        //访问微信的资源服务器，获取用户信息
        if(r.getCode()==0){
         //   r.getData("data", new TypeReference<MemberResponseVo>() {});

            String data = JSON.toJSONString(r.get("data"));
           MemberResponseVo memberResponseVo =JSON.parseObject(data,new TypeReference<MemberResponseVo>() {});

            log.info("登录成功：用户信息：{}",memberResponseVo);

            //1、第一次使用session，命令浏览器保存卡号，JSESSIONID这个cookie
            //以后浏览器访问哪个网站就会带上这个网站的cookie
            //TODO 1、默认发的令牌。当前域（解决子域session共享问题）
            //TODO 2、使用JSON的序列化方式来序列化对象到Redis中
            session.setAttribute(AuthServerConstant.LOGIN_USER,memberResponseVo);

            //2、登录成功跳回首页

            //return  "login";
            //return "redirect:http://auth.gulimall.com/login.html";


            return "forward:/tetx";
          //  return "redirect:http://gulimall.com";
        }else {

            return "redirect:http://auth.gulimall.com/login.html";
        }


    }


    @GetMapping("login")
    public String genQrConnect(HttpSession session) {
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        // 回调地址
        String redirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL; //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            //throw new GgktExceptionHander(20001, e.getMessage());
        }
        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "liuyyds";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
        System.out.println("state = " + state);
        // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键："wechar-open-state-" + httpServletRequest.getSession().getId()
        //值：satte
        //过期时间：30分钟

        //生成qrcodeUrl
        String qrcodeUrl = String.format(
                baseUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                redirectUrl,state);
        return "redirect:" + qrcodeUrl;
    }
}
