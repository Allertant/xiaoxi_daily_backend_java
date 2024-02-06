package icu.shiyixi.dailybackend.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import icu.shiyixi.dailybackend.bean.Admin;
import icu.shiyixi.dailybackend.common.Constants;
import icu.shiyixi.dailybackend.common.PropertiesConstants;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.utils.SMSUtils;
import icu.shiyixi.dailybackend.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {
    @Autowired
    private DefaultKaptcha kaptcha;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PropertiesConstants propertiesConstants;

    @GetMapping("/vcode")
    public R<String> getVcode(){
        String text = kaptcha.createText();
        BufferedImage img = kaptcha.createImage(text);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img,"jpg",os);
            //验证文本redis
            stringRedisTemplate.opsForValue().set(text,text,15, TimeUnit.MINUTES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String vcode = "data:image/jpeg;base64,"+ Base64Utils.encodeToString(os.toByteArray());
        //调用service层方法
        return R.success(vcode);
    }

    @GetMapping("downloadApk")
    public void download(@RequestParam() String version, HttpServletResponse response) {
        try {
            // path是指想要下载的文件的路径
            String uploadFilePath = propertiesConstants.getUploadFilePath();
            File file = new File(uploadFilePath + "/" + Constants.exeName + "-" + version + ".apk");
            log.info(file.getPath());
            // 获取文件名
            String filename = file.getName();
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @PostMapping("/code")
    public R<?> code(@RequestBody Admin admin) {
        String phone = admin.getPhone();
        // 生成随机的4位验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("验证码为code={}", code);
        // 调用阿里云提供的短信服务验证API完成发送短信
        SMSUtils.sendMessage("小兮ruiji", "SMS_462575544", phone, code);
        stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        return R.success("发送成功");
    }
}
