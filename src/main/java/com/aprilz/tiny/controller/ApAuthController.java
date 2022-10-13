package com.aprilz.tiny.controller;

import cn.hutool.core.util.IdUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.cache.Cache;
import com.aprilz.tiny.common.cache.CachePrefix;
import com.aprilz.tiny.common.cache.limit.annotation.LimitPoint;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mbg.entity.ApAdmin;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
public class ApAuthController {

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private Cache cache;

    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @LimitPoint(name = "kaptcha", key = "verification")
    @GetMapping("/kaptcha")
    @ApiOperation(value = "获取验证码接口,一分钟同一个ip请求10次")
    public CommonResult kaptcha(HttpServletRequest request) {
        CommonResult kaptcha = doKaptcha(request);
        if (kaptcha != null) {
            return kaptcha;
        }
        return CommonResult.error();
    }

    private CommonResult doKaptcha(HttpServletRequest request) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        String uuid = IdUtil.fastSimpleUUID();
        //2min
        cache.put(uuid, text, 120L);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            HashMap<String, String> map = new HashMap<>();
            map.put("uuid", uuid);
            map.put("image", "data:image/jpeg;base64," + base64.replaceAll("\r\n", ""));
            return CommonResult.success(map);
        } catch (IOException e) {
            return null;
        }
    }

    @GetMapping("/info")
    public CommonResult info(@RequestParam("token") String token) {
        // JwtAuthenticationTokenFilter set
        ApAdmin admin = UserUtil.getUser();
        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());
        data.put("avatar", admin.getAvatar());
        return CommonResult.success(data);
    }


    @PostMapping("/logout")
    public CommonResult logout(@RequestParam("token") String token) {
        String authToken = token.substring(this.tokenHead.length()).trim();
        cache.remove(CachePrefix.AUTH_TOKEN + authToken);
        cache.remove(CachePrefix.REFRESH_TOKEN + authToken);
        return CommonResult.success();
    }



}
