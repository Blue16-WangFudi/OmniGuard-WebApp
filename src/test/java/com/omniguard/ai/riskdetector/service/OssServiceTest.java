package com.omniguard.ai.riskdetector.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootTest
public class OssServiceTest {

    @Autowired
    OssService ossService;

    @Test
    public void uploadFromUrl(){
        ossService.uploadFromUrl("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg","exampledir/image.png");
    }

    @Test
    public void uploadString(){
        ossService.uploadString("Hello OSS, 你好世界", "exampledir/exampleobject.txt");
    }

    @Test
    public void uploadFile(){
        ossService.uploadFile("E:\\OneDrive\\DevWorkspace\\OmniGuard\\OmniGuard-WebApp\\src\\test\\java\\OssTestFile.txt","exampledir/fileTest.txt");
    }

    @Test
    public void uploadByteArray(){
        ossService.uploadByteArray("Hello OSS, 你好世界".getBytes(), "exampledir/exampleByteArray.txt");
    }

    @Test
    void downloadFile() {
        ossService.downloadFile("exampledir/image.png","E:\\test.png");
    }

    @Test
    void downloadBytes() throws FileNotFoundException {
        ossService.downloadBytes("exampledir/image.png", byteData -> {
            System.out.println("Received byte data of length: " + byteData.length);
        });
    }
}
