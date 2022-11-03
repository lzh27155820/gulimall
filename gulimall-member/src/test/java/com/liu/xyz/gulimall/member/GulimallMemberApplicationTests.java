package com.liu.xyz.gulimall.member;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liu.xyz.gulimall.member.entity.MemberEntity;
import com.liu.xyz.gulimall.member.service.MemberService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class GulimallMemberApplicationTests {

    @Test
    void contextLoads() {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);

    }
    @Test
    public void contextLoad() {

        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);
        System.out.println(Md5Crypt.md5Crypt("123456".getBytes()));
        System.out.println(Md5Crypt.md5Crypt("123456".getBytes(),"$1$qqqqqqqq"));

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        //$2a$10$GT0TjB5YK5Vx77Y.2N7hkuYZtYAjZjMlE6NWGE2Aar/7pk/Rmhf8S
        //$2a$10$cR3lis5HQQsQSSh8/c3L3ujIILXkVYmlw28vLA39xz4mHDN/NBVUi
        String encode = bCryptPasswordEncoder.encode("123456");
        boolean matches = bCryptPasswordEncoder.matches("123456", "$2a$10$GT0TjB5YK5Vx77Y.2N7hkuYZtYAjZjMlE6NWGE2Aar/7pk/Rmhf8S");

        System.out.println(encode+"==>" + matches);
    }

    @Autowired
    private MemberService memberService;
    @Test
    public void tessst(){
        MemberEntity one = memberService.getOne(new QueryWrapper<MemberEntity>().eq("social_uid", 1));
        System.out.println(one);
    }
}
