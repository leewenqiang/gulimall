package com.atguigu.gulimall.auth.controlor;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.auth.feign.MemberFeiginService;
import com.atguigu.gulimall.auth.feign.ThirdPartyFeignService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName LoginControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/15 14:02
 * @Version 1.0
 */

@Slf4j
@Controller
public class LoginControlor {


    public static final int ONE_MINITER = 60000;
    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    MemberFeiginService memberFeiginService;

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        if(session.getAttribute(AuthConstant.LOGIN_USER)!=null){
            return "redirect:http://gulimall.com";
        }else{
            return "login";
        }
    }

    @ResponseBody
    @RequestMapping("/sms/sendcode")
    public R sendSmsCode(@RequestParam("phone") String phone){



        //1、TODO 接口防刷
        //2、验证码的再次校验

        String reidCode = redisTemplate.opsForValue().get( AuthConstant.SMS_CODE_CACHE_PREFIX+phone);
        if(StringUtils.isNotEmpty(reidCode)){
            long time = Long.parseLong(reidCode.split("_")[1]);
            if((System.currentTimeMillis()-time)< ONE_MINITER){
                //60秒内不能再发
                return R.error(BizCodeEnum.VALID_SMS_CODE.getCode(),BizCodeEnum.VALID_SMS_CODE.getMsg());
            }
        }

        String orgaiganlCode = UUID.randomUUID().toString().substring(0, 5);
        String code = orgaiganlCode+"_"+System.currentTimeMillis();
        log.info("验证码:"+code);

        // key sms:code:手机号  值:验证码
        //缓存 验证码
        redisTemplate.opsForValue().set( AuthConstant.SMS_CODE_CACHE_PREFIX+phone,code,10, TimeUnit.MINUTES);
        //防止60秒 内再次发送验证码


        thirdPartyFeignService.sendCode(phone,orgaiganlCode);
        return R.ok();
    }


    /**
     * RedirectAttributes 重定向携带数据
     * @param userRegistVo
     * @param result
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid  UserRegistVo userRegistVo, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes, HttpSession httpSession){
        if(result.hasErrors()){

            Map<String,String> map = new HashMap<>();

//            Map<String, String> collect =
//                    result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
//                            FieldError::getDefaultMessage));;
//
            for (FieldError fieldError : result.getFieldErrors()) {

                map.put(fieldError.getField(),fieldError.getDefaultMessage());

            }

            redirectAttributes.addFlashAttribute("errors",map);
//           model.addAttribute("errors",map);



//            result.getFieldErrors().stream().collect()

            return "redirect:http://auth.gulimall.com/reg.html";
//            return "forward:/reg.html";
        }

        //注册
        //调用远程服务进行注册
        //1、校验验证码
        String s = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + userRegistVo.getPhone());
        if(StringUtils.isNotEmpty(s)){
            if(userRegistVo.getCode().equals(s.split("_")[0])){
                //删除验证码
                redisTemplate.delete( redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + userRegistVo.getPhone()));

                //调用远程服务注册
                R regist = memberFeiginService.regist(userRegistVo);
                if(regist.getCode()==0){
                    //成功
                    return "redirect:http://auth.gulimall.com/login.html";
                }else{
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("msg",regist.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }


            }else{

                HashMap<String, String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.gulimall.com/reg.html";

            }
        }else{
            HashMap<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

    }

    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo,RedirectAttributes redirectAttributes,HttpSession httpSession){
        R login = memberFeiginService.login(userLoginVo);
        if(login.getCode()==0){
            MemberResponseVo data = login.getData(new TypeReference<MemberResponseVo>() {
            });
            httpSession.setAttribute(AuthConstant.LOGIN_USER,data);
            //远程登录
            return "redirect:http://gulimall.com";
        }else{
            //失败
            HashMap<String, String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }

}
