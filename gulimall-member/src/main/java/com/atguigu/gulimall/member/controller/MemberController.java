package com.atguigu.gulimall.member.controller;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import com.atguigu.gulimall.member.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author lwq
 * @email lwqmrl@163.com
 * @date 2020-12-04 16:25:33
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/conpons")
    public R test(){
        MemberEntity entity = new MemberEntity();
        entity.setUsername("测试");
        return R.ok().put("member",entity).put("conpons", couponFeignService.memberCoupons().get("comons"));
    }


    @PostMapping("/regist")
    public R regist(@RequestBody UserRegistVo userRegistVo){
        try {
            memberService.regist(userRegistVo);
        }catch (UserNameExistException e){
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(),BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }catch (PhoneExistException e){
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(),BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){

        MemberEntity memberEntity = memberService.login(memberLoginVo);

        if(memberEntity != null){
            return R.ok().setData(memberEntity);
        }else{
            return R.error(BizCodeEnum.LOGIN_AACCOUNT_VALID_EXCEPTION.getCode(),BizCodeEnum.LOGIN_AACCOUNT_VALID_EXCEPTION.getMsg());
        }

    }

    @PostMapping("/social/login")
    public R socialLogin(@RequestBody SocialUser socialUser){

        MemberEntity memberEntity = memberService.socialLogin(socialUser);
        if(memberEntity != null){
            return R.ok().setData(memberEntity);
        }else{
            return R.error(BizCodeEnum.LOGIN_AACCOUNT_VALID_EXCEPTION.getCode(),BizCodeEnum.LOGIN_AACCOUNT_VALID_EXCEPTION.getMsg());
        }

    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
