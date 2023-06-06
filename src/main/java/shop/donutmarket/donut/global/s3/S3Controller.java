package shop.donutmarket.donut.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.donutmarket.donut.global.util.S3KeyGenerator;

import java.io.IOException;

@RestController
@RequestMapping("/S3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping
    ResponseEntity<?> download(@RequestParam String key) {
        return s3Service.download(key);
    }
}
