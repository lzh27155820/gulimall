package com.liu.xyz.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.liu.xyz.common.productUtils.AuthServerConstant;
import com.liu.xyz.common.productUtils.BizCodeEnume;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.common.vo.MemberResponseVo;
import com.liu.xyz.feign.MemberFeignService;
import com.liu.xyz.feign.ThirdPartyFeignService;
import com.liu.xyz.vo.UserLoginVo;
import com.liu.xyz.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.liu.xyz.common.productUtils.AuthServerConstant.LOGIN_USER;

/**
 * create liu 2022-10-27
 */
@Slf4j
@Controller
public class LoginController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;
    @Autowired
    private MemberFeignService memberFeignService;
    /**
     *  发送验证码
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone){
        /**
         *  1.接口防刷
         *      可用redis 先根据手机号get出验证码 如果没有就发送 如果有就提示
         *  2.数据校验
         *      用redis就行
         为了跟数据一样才写成一下
         */
        //1、接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            //活动存入redis的时间，用当前时间减去存入redis的时间，判断用户手机号是否在60s内发送验证码
            long currentTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - currentTime < 60000) {
                //60s内不能再发
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        //2、验证码的再次效验 redis.存key-phone,value-code
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        String codeNum = String.valueOf(code);
        String redisStorage = codeNum + "_" + System.currentTimeMillis();

        //存入redis，防止同一个手机号在60秒内再次发送验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,
                redisStorage,10, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone, codeNum);
        return R.ok();
    }



    /**
     * 注册
     * @param userRegisterVo    注册类
     * @param redirectAttributes 重定向带数据类
     * @param bindingResult 校验结果
     * @return
     */

    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVo userRegisterVo, RedirectAttributes redirectAttributes
    , BindingResult bindingResult){
        //判断注册信息是否异常
        if(bindingResult.hasErrors()){
            Map<String, String> map = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            //
            redirectAttributes.addAttribute("errors",map);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());

        if(StringUtils.isEmpty(s)){
            //效验出错回到注册页面
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码过期失效");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        s = s.substring(0, 6);
        if(!s.equals(userRegisterVo.getCode())){
            //效验出错回到注册页面
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        log.info("注册信息 {}",userRegisterVo);
        //调用远程服务member
        R register = memberFeignService.register(userRegisterVo);
        if(register.getCode()==0){

            //成功
            return "redirect:http://auth.gulimall.com/login.html";
        }else{

            HashMap<String, String> errors = new HashMap<>();

            Object msg = register.get("msg");
            String s1 = JSON.toJSONString(msg);
            String s2 = JSON.parseObject(s1, String.class);
            errors.put("msg",s2);

            redirectAttributes.addAttribute("errors",errors);

            return "redirect:http://auth.gulimall.com/reg.html";

        }
    }
    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session) {

        //从session先取出来用户的信息，判断用户是否已经登录过了
        Object attribute = session.getAttribute(LOGIN_USER);
        //如果用户没登录那就跳转到登录页面
        if (attribute == null) {
            return "login";
        } else {
            return "redirect:http://gulimall.com";
        }

    }


    @PostMapping(value = "/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session) {

        //远程登录
        R login = memberFeignService.login(vo);

        if (login.getCode() == 0) {

            String data = JSON.toJSONString(login.get("data"));
            MemberResponseVo member = JSON.parseObject(data, new TypeReference<MemberResponseVo>() {
            });

            session.setAttribute(LOGIN_USER,member);
            return "redirect:http://gulimall.com";
        } else {
            Map<String,String> errors = new HashMap<>();

            String msg = JSON.toJSONString(login.get("msg"));
            String s = JSON.parseObject(msg, String.class);
            errors.put("msg",s);
            attributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }


}
