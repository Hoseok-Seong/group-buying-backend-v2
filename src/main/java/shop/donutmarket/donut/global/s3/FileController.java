package shop.donutmarket.donut.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

//    @Autowired
//    private S3Service fileController2;

    private final S3Service s3Service;

    @PostMapping
    ResponseEntity<?> upload(MultipartFile multipartFile) throws IOException {
        s3Service.upload(multipartFile);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    ResponseEntity<?> download(@RequestParam String key) {
        ResponseEntity<?> response  = s3Service.download(key);
        return response;
    }
}
