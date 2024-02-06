package icu.shiyixi.dailybackend.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class PropertiesConstants {

    @Value("${system.upload-filepath}")
    private String uploadFilePath;

    @Value("${system.view-path}")
    private String viewPath;

    @Value("${system.expire-time}")
    private Long expireTime;
}
