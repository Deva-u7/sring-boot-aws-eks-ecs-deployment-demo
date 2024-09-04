package com.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URISyntaxException;
import java.time.Duration;

@Configuration
public class AWSS3Config {

    private String region = "eu-west-1";

    @Bean("S3Client")
    public S3Client getAmazonS3Client() throws URISyntaxException {
        AwsBasicCredentials credentials = AwsBasicCredentials.create("test", "test");
        final StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        S3Client client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
        return client;
    }

    @Bean("S3Presigner")
    public S3Presigner getAmazonS3Presigner() {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .build();
        return presigner;
    }

    @Bean("S3AsyncClient")
    public S3AsyncClient getS3AsyncClient() {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(900))
                .connectionAcquisitionTimeout(Duration.ofSeconds(900))
                .connectionMaxIdleTime(Duration.ofSeconds(900))
                .maxConcurrency(10)
                .maxPendingConnectionAcquires(1000)
                .tcpKeepAlive(true)
                .connectionTimeToLive(Duration.ofSeconds(900))
                .writeTimeout(Duration.ofSeconds(900))
                .readTimeout(Duration.ofSeconds(900))
                .build();

        S3Configuration serviceConfiguration = S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();

        S3AsyncClient client = S3AsyncClient.builder()
                .region(Region.of(region))
                .httpClient(httpClient)
                .serviceConfiguration(serviceConfiguration)
                .build();
        return client;
    }
}