package shop.donutmarket.donut.global.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class S3KeyGenerator {

    public static String makeKey(MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();
        long time = System.currentTimeMillis();
        return uuid.toString()+time+multipartFile.getOriginalFilename();
    }
}
