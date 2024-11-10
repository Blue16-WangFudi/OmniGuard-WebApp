package com.omniguard.ai.riskdetector.service;

import com.aliyun.oss.*;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.function.Consumer;

import java.io.*;
import java.net.URL;

@Service
public class OssService {
    @Value("${aliyun.oss.bucketname}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    String endpoint;

    private final OSS ossClient;

    public OssService(@Value("${aliyun.oss.accessKeyId}") String accessKeyId,
                      @Value("${aliyun.oss.accessKeySecret}") String accessKeySecret,
                      @Value("${aliyun.oss.endpoint}") String endpoint) {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public void uploadFromUrl(String url, String objectName) {
        try (InputStream inputStream = new URL(url).openStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            handleOssException(oe);
        } catch (ClientException | IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void uploadFile(String filePath, String objectName) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new File(filePath));
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            handleOssException(oe);
        } catch (ClientException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void uploadString(String content, String objectName) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            handleOssException(oe);
        } catch (ClientException | IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void uploadByteArray(byte[] content, String objectName) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            handleOssException(oe);
        } catch (ClientException | IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void downloadFile(String objectName, String destinationPath) {
        try {
            ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(destinationPath));
        } catch (OSSException oe) {
            handleOssException(oe);
        } catch (ClientException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void downloadBytes(String objectName, Consumer<byte[]> byteCallback) {
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            try (InputStream inputStream = ossObject.getObjectContent();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);

                    // Call the callback function with the current byte array
                    if (byteCallback != null) {
                        byteCallback.accept(buffer);
                    }
                }
                outputStream.flush();
            }
            ossObject.close();
        } catch (OSSException oe) {
            handleOssException(oe);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public String getRealURL(String objectName) {

        // https://BucketName.Endpoint/ObjectName
        return "https://"+bucketName+"."+endpoint+"/"+objectName;
    }

    private void handleOssException(OSSException oe) {
        System.err.println("OSSException: " + oe.getErrorMessage());
        System.err.println("Error Code: " + oe.getErrorCode());
        System.err.println("Request ID: " + oe.getRequestId());
        System.err.println("Host ID: " + oe.getHostId());
    }

    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}

