package shop.donutmarket.donut.global.aws;

import java.io.File;

import org.springframework.context.annotation.Configuration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import shop.donutmarket.donut.global.exception.Exception500;

@Configuration
public class FileLoad {

    private final AmazonS3 amazonS3Client;

    private String bucketName = "groupbuying3";

    public FileLoad(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }


    public String uploadFile(String key, String filePath) {
        TransferManager transferManager = TransferManagerBuilder.standard()
            .withS3Client(amazonS3Client)
            .build();

        Upload upload = transferManager.upload(this.bucketName, key, new File(filePath));

        try {
            UploadResult uploadResult = upload.waitForUploadResult();

            return uploadResult.getETag();
        } catch (AmazonServiceException e) {
            throw new Exception500("아마존 서비스에 오류가 발생하였습니다.");
        } catch (AmazonClientException e) {
            throw new Exception500("아마존 클라이언트에 오류가 발생하였습니다.");
        } catch (InterruptedException e) {
            throw new Exception500("업로드에 실패하였습니다.");
        }
    }

    public String downloadObject(String key) {
        try {
            ObjectMetadata metadata = amazonS3Client.getObjectMetadata(bucketName, key);
            return amazonS3Client.getUrl(this.bucketName, key).toString();
        } catch (AmazonS3Exception e) {
            throw new Exception500("이미지 다운로드에 실패했습니다.");
        }
    }

}
