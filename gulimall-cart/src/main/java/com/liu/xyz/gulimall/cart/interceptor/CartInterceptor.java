package com.liu.xyz.gulimall.cart.interceptor;

import com.liu.xyz.common.productUtils.AuthServerConstant;
import com.liu.xyz.common.productUtils.CartConstant;
import com.liu.xyz.common.vo.MemberResponseVo;
import com.liu.xyz.gulimall.cart.to.UserInfoTo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 *  实现mvc的拦截器
 *
 * create liu 2022-11-02
 */

public class CartInterceptor implements HandlerInterceptor {
    /**
     *  主要解决的就是让每个线程绑定自己的值，可以将 ThreadLocal 类形象的比喻成存放数据的盒子，盒子中可以存储每个线程的私有数据。
     */
    public static ThreadLocal<UserInfoTo> threadLocal=new ThreadLocal<>() ;
    /**
     *  目标执行之前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();

        MemberResponseVo memberResponseVo =(MemberResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(memberResponseVo!=null){
            userInfoTo.setUserId(memberResponseVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        //获取零时登入用户信息
        if(cookies!=null&&cookies.length>0){

            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        //分配零时用户

        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }

        //保存到共享线程中
        threadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoTo userInfoTo = threadLocal.get();

        if(!userInfoTo.getTempUser()){

            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }



    }
}
