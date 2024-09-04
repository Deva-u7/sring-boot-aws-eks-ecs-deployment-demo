package com.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class AWSS3FileUploadService {

    @Autowired
    @Qualifier("S3Client")
    private S3Client amazonS3;

    public String fileUpload(MultipartFile multipartFile){
        String resourceUrl = null;
        String bucketName = "bucket";
        String basePath = "/bucket/";
        String path = "object";
        String fileName = "test.txt";
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            //  bucket/object/test.txt
            uploadResourceToS3Bucket(bucketName, file, String.format("/%s/%s",path, fileName));
            resourceUrl = generateResourceUrl(basePath, path, fileName);

            file.delete(); // To remove the file locally created in the project folder.
        } catch (final AwsServiceException ex) {
            ex.printStackTrace();
        }
        return resourceUrl;
//        return "https://s3.amazonaws.com/bucket/object";
    }

    private String generateResourceUrl(String basePath, String path, String fileName) {
        String resourceUrl;
        // resourceUrl = https://surreal-xpmanager-assets.s3.amazonaws.com/program-class/{class-id}/resource/<resource-name>/<filename>.<ext>
        resourceUrl = "https://s3.amazonaws.com/" + basePath + path + "/" + fileName;
        return resourceUrl;
    }

    private void uploadResourceToS3Bucket(String bucketName, File file, String uniqueFileNameWithPath) {
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileNameWithPath)
                .build();

        amazonS3.putObject(putObjectRequest,  RequestBody.fromFile(file));
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File("test");
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {

        }
        return file;
    }
}
