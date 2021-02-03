package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.service.MemberLevelService;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import com.atguigu.gulimall.member.vo.UserRegistVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(UserRegistVo userRegistVo) {

        MemberEntity entity = new MemberEntity();

        //检查用户名和手机号是否唯一

        memberLevelService.checkPhoneUnique(userRegistVo.getPhone());
        memberLevelService.checkUserNameUnique(userRegistVo.getUserName());
        entity.setUsername(userRegistVo.getUserName());
        entity.setMobile(userRegistVo.getPhone());

        entity.setNickname(userRegistVo.getUserName());

        //密码加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(userRegistVo.getPassword());
        entity.setPassword(encode);


        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        //默认会员等级
        if(memberLevelEntity!=null){
           entity.setLevelId(memberLevelEntity.getId());
        }



        baseMapper.insert(entity);

    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {


        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        //查询数据库
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", memberLoginVo.getLoginAccount()).or().eq("mobile", memberLoginVo.getLoginAccount()));
        if(memberEntity == null){
            //登录失败
            return null;
        }

        String passwordDb = memberEntity.getPassword();
        boolean matches = bCryptPasswordEncoder.matches(memberLoginVo.getPassword(), passwordDb);
        if(!matches){
            //登录失败
            return null;
        }else{
            return memberEntity;
        }



    }

    @Override
    public MemberEntity socialLogin(SocialUser socialUser) {
        String uid = socialUser.getUid();
        //查询当前社交用户在系统 中是否存在
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("uid", uid));
        if(memberEntity != null){
            //社交用户存在
            //更新accessToken和过期时间
            MemberEntity entity = new MemberEntity();
            entity.setId(memberEntity.getId());
            entity.setAccess_token(socialUser.getAccess_token());
            entity.setExpires_in(socialUser.getExpires_in());
            baseMapper.updateById(entity);

            memberEntity.setExpires_in(socialUser.getExpires_in());
            memberEntity.setAccess_token(socialUser.getAccess_token());
            return memberEntity;

        }else{
            //进行注册
            MemberEntity newMember = new MemberEntity();
            newMember.setAccess_token(socialUser.getAccess_token());
            newMember.setExpires_in(socialUser.getExpires_in());
            newMember.setUid(socialUser.getUid());

            //获取昵称和性别等其他社交信息
            try{
                HashMap<String, String> query = new HashMap<>();
                query.put("access_token",socialUser.getAccess_token());
                query.put("uid",socialUser.getUid());
                HttpResponse res = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);

                if(res.getStatusLine().getStatusCode()==200){
                    //成功
                    String s = EntityUtils.toString(res.getEntity());
                    JSONObject jsonObject = JSON.parseObject(s);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    newMember.setNickname(name);
                    newMember.setGender("m".equalsIgnoreCase(gender)?1:0);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            //新增
            baseMapper.insert(newMember);
            return newMember;

        }
    }

}