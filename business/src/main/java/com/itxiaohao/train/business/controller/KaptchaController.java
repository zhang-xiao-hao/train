package com.itxiaohao.train.business.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @Author: itxiaohao
 * @date: 2023-09-28 14:56
 * @Description:
 */
@RestController
@RequestMapping("/kaptcha")
public class KaptchaController {
    @Qualifier("getDefaultKaptcha")
    @Autowired
    DefaultKaptcha defaultKaptcha;
    @Resource
    public StringRedisTemplate stringRedisTemplate;

    @GetMapping("/image-code/{imageCodeToken}")
    public void imageCode(
            @PathVariable(value = "imageCodeToken") String imageCodeToken,
            HttpServletResponse httpServletResponse) throws Exception{
        ByteArrayOutputStream jpegOutPutStream = new ByteArrayOutputStream();
        try{
            // 生成验证码
            String text = defaultKaptcha.createText();
            // 将生成的验证码放入缓存，后续验证时用
            stringRedisTemplate.opsForValue().set(imageCodeToken, text, 300, TimeUnit.SECONDS);

            // 使用验证码生成验证码图片
            BufferedImage challenge = defaultKaptcha.createImage(text);
            ImageIO.write(challenge, "jpg", jpegOutPutStream);
        }catch (IllegalArgumentException e){
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        byte[] captchaChallengeAsJpeg = jpegOutPutStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma","no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}
