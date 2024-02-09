package icu.shiyixi.dailybackend.common;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class CommonTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DefaultKaptcha kaptcha;
    @Test
    public void testVcode() {
        String text = "请重新点击";
        BufferedImage img = kaptcha.createImage(text);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img,"jpg",os);
            //验证文本redis
            // stringRedisTemplate.opsForValue().set(text,text,15, TimeUnit.MINUTES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String vcode = "data:image/jpeg;base64,"+ Base64Utils.encodeToString(os.toByteArray());
        System.out.println(vcode);
    }
}
