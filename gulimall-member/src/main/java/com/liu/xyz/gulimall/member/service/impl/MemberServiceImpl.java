package com.liu.xyz.gulimall.member.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.common.vo.MemberResponseVo;
import com.liu.xyz.gulimall.member.config.GiteeHttpClient;
import com.liu.xyz.gulimall.member.config.HttpClientUtils;
import com.liu.xyz.gulimall.member.dao.MemberDao;
import com.liu.xyz.gulimall.member.dao.MemberLevelDao;
import com.liu.xyz.gulimall.member.entity.MemberEntity;
import com.liu.xyz.gulimall.member.entity.MemberLevelEntity;
import com.liu.xyz.gulimall.member.ex.PhoneException;
import com.liu.xyz.gulimall.member.ex.UsernameException;
import com.liu.xyz.gulimall.member.service.MemberService;
import com.liu.xyz.gulimall.member.vo.MemberUserLoginVo;
import com.liu.xyz.gulimall.member.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(UserRegisterVo userRegisterVo) {
        MemberEntity member = new MemberEntity();

        //1.设置基本进行，和默认会员等级
        member.setUsername(userRegisterVo.getUserName());

        member.setMobile(userRegisterVo.getPhone());
        MemberLevelEntity memberLevelEntity = memberLevelDao.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        member.setLevelId(memberLevelEntity.getId());
        //判断手机号和用户名是否唯一
        checkPhoneUique(userRegisterVo.getPhone());
        checkUserNameUique(userRegisterVo.getUserName());
        //密码Md5盐值加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(userRegisterVo.getPassword());
        member.setPassword(encode);
        member.setCreateTime(new Date());
        member.setGender(0);
        member.setNickname(userRegisterVo.getUserName());
        this.save(member);

    }

    @Override
    public void checkPhoneUique(String phone) throws PhoneException {

        Long mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(mobile>0){
            throw new PhoneException();
        }
    }

    @Override
    public void checkUserNameUique(String userName) throws UsernameException {
        Long username = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(username>0){
            throw new UsernameException();
        }
    }

    @Override
    public MemberEntity login(MemberUserLoginVo vo) {

        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", vo.getLoginacct())
                .or().eq("mobile", vo.getLoginacct()));
        if(memberEntity==null){
            return null;
        }


        String password = memberEntity.getPassword();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(vo.getPassword(), password);
        if(matches){
            return memberEntity;
        }


        return null;
    }

    @Override
    public MemberEntity weixinLogin(String openid,String accessToken) {


        MemberEntity member = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", openid));
        if(member==null){
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=" +accessToken+
                    "&openid="+openid;
            //String format = String.format(baseUserInfoUrl, accessToken, openid);

            String resultUserInfo = null;
            try {
                //获取用户信息
                resultUserInfo = HttpClientUtils.get(baseUserInfoUrl);
                System.out.println("========="+resultUserInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            HashMap map = gson.fromJson(resultUserInfo, HashMap.class);

            String nickName = (String) map.get("nickname");
            String headimgurl = (String) map.get("headimgurl");
        //    String nickName = (String) userInfoMap.get("nickname");      //昵称
            Double sex = (Double) map.get("sex");        //性别
            //String headimgurl = (String) map.get("headimgurl");      //微信头像

             member = new MemberEntity();

            member = new MemberEntity();
            member.setNickname(nickName);
            member.setGender(Integer.valueOf(Double.valueOf(sex).intValue()));
            member.setHeader(headimgurl);
            member.setCreateTime(new Date());
            member.setSocialUid(openid);
            member.setAccessToken(accessToken);

            MemberLevelEntity memberLevelEntity = memberLevelDao.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
            member.setLevelId(memberLevelEntity.getId());


            this.save(member);
        }



        return member;
    }

    @Override
    public MemberEntity gitee(MemberResponseVo memberResponseVo) {

        String socialUid = memberResponseVo.getSocialUid();
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", socialUid));
        if(memberEntity==null){

             memberEntity = new MemberEntity();
            MemberLevelEntity memberLevelEntity = memberLevelDao.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
            memberEntity.setLevelId(memberLevelEntity.getId());
           memberEntity.setNickname(memberResponseVo.getNickname());
           memberEntity.setSocialUid(socialUid);
            memberEntity.setCreateTime(new Date());
           this.save(memberEntity);
        }


        return memberEntity;
    }

    @Override
    public MemberEntity giteeLogin(String url) {
        try {
            JSONObject jsonObject = GiteeHttpClient.getUserInfo(url);

            Integer id =(Integer) jsonObject.get("id");

            MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", id+""));

            if(memberEntity==null){

                memberEntity=new MemberEntity();

                memberEntity.setNickname(jsonObject.getString("name"));
                memberEntity.setSocialUid(id+"");
                MemberLevelEntity memberLevelEntity = memberLevelDao.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
                memberEntity.setLevelId(memberLevelEntity.getId());
                this.save(memberEntity);
            }
            return memberEntity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}