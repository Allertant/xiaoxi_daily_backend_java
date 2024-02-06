package icu.shiyixi.dailybackend.config;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码生成器的效果
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha defaultKaptcha(){
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties prop = new Properties();
        prop.setProperty(Constants.KAPTCHA_BORDER,"no");
        prop.setProperty(Constants.KAPTCHA_IMAGE_WIDTH,"200");
        prop.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT,"50");
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR,"red");
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING,"0123456789abcdefghijklmn");
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH,"4");
        Config config = new Config(prop);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
