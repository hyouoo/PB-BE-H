package com.example.purebasketbe.global.s3;


import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Handler {

    private final S3Template s3Template;

    @Value("${aws.bucket.name}")
    private String bucket;
    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String makeUrl(MultipartFile file) {
        String key = getRandomKey(file);
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }

    public void uploadImages(String imgUrl, MultipartFile file) {
        String key = getKey(imgUrl);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_IMAGE);
        }
        ObjectMetadata metadata = ObjectMetadata.builder().contentType("text/plain").build();

        s3Template.upload(bucket, key, inputStream, metadata);
    }

    public void uploadImages(List<String> imgUrlList, List<MultipartFile> files) {
        for (int i = 0; i < imgUrlList.size(); i++) {
            uploadImages(imgUrlList.get(i), files.get(i));
        }
    }

    public void deleteImage(String imgUrl) {
        String key = getKey(imgUrl);
        s3Template.deleteObject(bucket, key);
    }

    private String getRandomKey(MultipartFile file) {
        return UUID.randomUUID() + "_" + file.getOriginalFilename();
    }
    private String getKey(String imgUrl) {
        int lastIndex = imgUrl.lastIndexOf("/");
        if (lastIndex != -1) {
            return imgUrl.substring(lastIndex + 1);
        } else throw new CustomException(ErrorCode.INVALID_IMAGE);
    }
}
