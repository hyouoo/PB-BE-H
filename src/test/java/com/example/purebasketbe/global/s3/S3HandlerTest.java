package com.example.purebasketbe.global.s3;

import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3HandlerTest {
    @Mock
    S3Template s3Template;

    @InjectMocks
    S3Handler s3Handler;


    @Test
    @DisplayName("파일에서 url 생성")
    void makeUrl() {
        // given
        MultipartFile file = mock(MultipartFile.class);

        // when
        String result = s3Handler.makeUrl(file);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("이미지 업로드 성공")
    void uploadImageSuccess() throws IOException {
        // given
        String imgUrl = "imgUrl/key";
        MultipartFile file = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);

        given(file.getInputStream()).willReturn(inputStream);

        // when
        s3Handler.uploadImages(imgUrl, file);

        // then
        verify(s3Template).upload(any(), anyString(), eq(inputStream), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("이미지 업로드 실패 - 유효하지 않은 키")
    void uploadImageFail1() {
        // given
        String imgUrl = "imgUrl";
        MultipartFile file = mock(MultipartFile.class);

        // when
        Exception exception = assertThrows(CustomException.class,
                () -> s3Handler.uploadImages(imgUrl, file)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_IMAGE.getMessage());
    }

    @Test
    @DisplayName("이미지 업로드 실패 - 유효하지 않은 이미지")
    void uploadImageFail2() throws IOException {
        // given
        String imgUrl = "imgUrl/key";
        MultipartFile file = mock(MultipartFile.class);
        given(file.getInputStream()).willThrow(IOException.class);

        // when
        Exception exception = assertThrows(CustomException.class,
                () -> s3Handler.uploadImages(imgUrl, file)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_IMAGE.getMessage());
    }

    @Test
    @DisplayName("여러 이미지 업로드 성공")
    void uploadImagesSuccess() {
        // given
        List<String> imgUrlList = List.of("imgUrl/key1", "imgUrl/key2");
        List<MultipartFile> files = List.of(mock(MultipartFile.class), mock(MultipartFile.class));

        // when
        s3Handler.uploadImages(imgUrlList, files);

        // then
        // verify(s3Handler, times(imgUrlList.size())).uploadImages(anyString(), any(MultipartFile.class));
    }

    @Test
    @DisplayName("이미지 삭제")
    void deleteImage() {
        // given
        String imgUrl = "imgUrl/key";

        // when
        s3Handler.deleteImage(imgUrl);

        // then
        verify(s3Template).deleteObject(any(), anyString());

    }
}