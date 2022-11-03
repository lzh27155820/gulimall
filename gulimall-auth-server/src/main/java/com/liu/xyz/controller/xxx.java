package com.liu.xyz.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.liu.xyz.common.productUtils.AuthServerConstant;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.common.vo.MemberResponseVo;
import com.liu.xyz.config.GiteeHttpClient;
import com.liu.xyz.feign.MemberFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.UUID;

/***
 *  gitee 登入
 * create liu 2022-11-01
 */
@Controller
public class xxx {


    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/tetx")
    public String res(HttpSession httpSession){

        MemberResponseVo attribute =(MemberResponseVo) httpSession.getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute==null){
            throw  new RuntimeException("值为空");
        }
        httpSession.setAttribute(AuthServerConstant.LOGIN_USER,attribute);

        return  "redirect:http://gulimall.com";
    }




    /**
     * gitee授权中提供的 appid 和 appkey
     */
    @Value("${gitee.oauth.clientid}")
    public String CLIENTID;
    @Value("${gitee.oauth.clientsecret}")
    public String CLIENTSECRET;
    @Value("${gitee.oauth.callback}")
    public String URL;

    /**
     * 请求授权页面
     */
    @GetMapping(value = "/auth")
    public String qqAuth(HttpSession session) {
        // 用于第三方应用防止CSRF攻击
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        session.setAttribute("state", uuid);

        // Step1：获取Authorization Code

        String ss="https://gitee.com/oauth/authorize?response_type=code&client_id=9f49ec82d1ea87923abf5f2874165251dfec58600047b949dccc3def48b976d8&redirect_uri=http://auth.gulimall.com/gitee";
        String url = "https://gitee.com/oauth/authorize?response_type=code" +
                "&client_id=" + CLIENTID +
                "&redirect_uri=" + URLEncoder.encode(URL) +
                "&state=" + uuid +
                "&scope=user_info";
        //因为使用的是thymeleaf模板引擎，所以是无法解析一个网址的，只能重定向
        return "redirect:"+url;
    }
    /**
     * 授权回调
     */
    @GetMapping(value = "/gitee")
    public String qqCallback(HttpServletRequest request,HttpSession httpSession) throws Exception {
        HttpSession session = request.getSession();
        // 得到Authorization Code
        String code = request.getParameter("code");
        // 我们放在地址中的状态码
        String state = request.getParameter("state");
        String uuid = (String) session.getAttribute("state");



        // Step2：通过Authorization Code获取Access Token
        String url = "https://gitee.com/oauth/token?grant_type=authorization_code" +
                "&client_id=" + CLIENTID +
                "&client_secret=" + CLIENTSECRET +
                "&code=" + code +
                "&redirect_uri=" + URL;
        JSONObject accessTokenJson = GiteeHttpClient.getAccessToken(url);

        // Step3: 获取用户信息
        url = "https://gitee.com/api/v5/user?access_token=" + accessTokenJson.get("access_token");


        R r =memberFeignService.giteeLogin( url);

        //JSONObject jsonObject = GiteeHttpClient.getUserInfo(url);
        /**
         * 获取到用户信息之后，就该写你自己的业务逻辑了
         *
         */

//        Integer id =(Integer) jsonObject.get("id");
//        String name = jsonObject.getString("name");
//
//        MemberResponseVo memberResponseVo = new MemberResponseVo();

//        memberResponseVo.setNickname(name);
//        memberResponseVo.setSocialUid(id+"");
//       R r = memberFeignService.gitee(memberResponseVo);


        //String data = JSON.toJSONString(r.get("data"));
        String data = JSON.toJSONString(r.get("data"));
        MemberResponseVo member =JSON.parseObject(data,new TypeReference<MemberResponseVo>() {});

        httpSession.setAttribute(AuthServerConstant.LOGIN_USER,member);
    //    System.out.println(jsonObject);


        return  "redirect:http://gulimall.com";
    }
}
