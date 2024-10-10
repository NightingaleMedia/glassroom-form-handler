package functions.services;

import com.google.api.services.storage.StorageScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.auth.Credentials;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class UploadService {

    private static String storageProjectId = "glassroom-413805";
    private static String storageBucketName = "my-bucket-name";
    Bucket bucket = null;

    Storage storage;

    public UploadService() {
        connectToCloudStorage();
    }

    public void createBucket(String newBucketName) {


        String bucketToName = "testbucket" + newBucketName;

//        if(bucketExists(bucketToName)) return;

        Bucket bucket = this.storage.create(BucketInfo.of(bucketToName));
    }

    public void uploadFile(String storageBucketRequestName, File file) {
        try {
            connectToCloudStorage();

            File tryFile = new File("test.txt");
            tryFile.renameTo()


            BlobId blobId = BlobId.of(storageBucketRequestName, file.getName());


        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage();
        }
    }

    private void store(File file, BlobId blobId) throws IOException {

        byte[] content = Files.readAllBytes(file.toPath());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        this.storage.createFrom(blobInfo, new ByteArrayInputStream(content));
    }

    boolean bucketExists(String bucketName) {
        Bucket potentialBucket = this.storage.get(bucketName);

        return potentialBucket != null;
    }

    void connectToCloudStorage() {
        if (this.bucket != null) return;

        try {
            Credentials credentials = GoogleCredentials.getApplicationDefault().createScoped(StorageScopes.all());

            this.storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId(this.storageProjectId)
                    .build()
                    .getService();

//            this.bucket = this.storage.get(this.storageBucketName);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
