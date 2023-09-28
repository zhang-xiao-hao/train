package com.itxiaohao.train.business.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author: itxiaohao
 * @date: 2023-09-28 14:46
 * @Description: 谷歌验证码配置文件
 */
@Configuration
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        //是否有边框  默认为true  我们可以自己设置yes，no
        properties.setProperty("kaptcha.border", "no");
        //边框颜色   默认为Color.BLACK
//        properties.setProperty("kaptcha.border.color", "105,179,90");
        //验证码文本字符颜色  默认为Color.BLACK
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        //验证码图片宽度  默认为200
        properties.setProperty("kaptcha.image.width", "90");
        //验证码图片高度  默认为50
        properties.setProperty("kaptcha.image.height", "28");
        // 验证码文本字符大小  默认为40
        properties.setProperty("kaptcha.textproducer.font.size", "20");
        //session key
        properties.setProperty("kaptcha.session.key", "code");
        // 验证码文本字符间距  默认为2
//        properties.setProperty("kaptcha.textproducer.char.spac", "35");
        //验证码文本字符长度  默认为5
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        //验证码文本字体样式  默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
        properties.setProperty("kaptcha.textproducer.font.names", "Arial");
        //验证码噪点颜色   默认为Color.BLACK
        properties.setProperty("kaptcha.noise.color", "288,96,0");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    @Bean
    public DefaultKaptcha getWebKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");
//        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "90");
        properties.setProperty("kaptcha.image.height", "45");
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.impl", "com.fc.test.common.support.KaptchaTextCreator");
//        properties.setProperty("kaptcha.textproducer.char.spac", "5");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.names", "Arial");
//        properties.setProperty("kaptcha.noise.color", "white");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}