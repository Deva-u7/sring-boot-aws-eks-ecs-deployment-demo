package com.todo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

@Configuration
public class AWSS3Config {

    private String region = "eu-test-1";

    @Bean("S3Client")
    public S3Client getAmazonS3Client() throws URISyntaxException {
        AwsBasicCredentials credentials = AwsBasicCredentials.create("test1", "test1");
        final StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        S3Client client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .endpointOverride(new URI("http://localhost:8080"))
                .build();
        return client;
    }

}