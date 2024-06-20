// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.noi.edisplay.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class FileImportStorageS3 {

    private final String bucket;
    private final S3Client s3Client;
    Logger logger = LoggerFactory.getLogger(FileImportStorageS3.class);

    public FileImportStorageS3(@Value("${aws.bucket.fileImport}") String bucket, S3Client s3Client) {
        this.bucket = bucket;
        this.s3Client = s3Client;
    }

    public void upload(byte[] bytes, String s3FileKey) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(s3FileKey).build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
    }

    public byte[] download(String s3FileKey) {

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(s3FileKey).build();
            final ResponseBytes<GetObjectResponse> object = s3Client.getObject(getObjectRequest,
                    ResponseTransformer.toBytes());
            return object.asByteArray();
            // Process the object as needed

        } catch (NoSuchKeyException e) {
            // Handle the specific case where the key does not exist
            System.err.println("The specified key does not exist: " + s3FileKey);
            // You can log this error or return a specific message to the client
        } catch (S3Exception e) {
            // Handle other S3 exceptions
            System.err.println("S3 Exception: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            // Handle any other exceptions
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
        return null;
    }

    public void copy(String oldS3FileKey, String newS3FileKey) {
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder().sourceBucket(bucket).sourceKey(oldS3FileKey)
                .destinationBucket(bucket).destinationKey(newS3FileKey).build();
        s3Client.copyObject(copyObjectRequest);
    }
}
