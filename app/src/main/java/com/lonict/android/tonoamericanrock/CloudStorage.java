package com.lonict.android.tonoamericanrock;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;

/**
 * Created by Efe Avsar on 10/08/2015.
 */
public class CloudStorage {

    private final String APPLICATION_NAME ;//= "com.lonict.android.tonoamericanrock";
    private final String BUCKET_NAME ;// = "lonict_android";
    private final String EMAIL_ADRESS ;//="tono_american_radio/radiostations.json";
    private final InputStream KEY_STREAM;
    private final String STORE_PASSWORD;
    private final String ALIAS;
    private final String KEY_PASSWORD;
    private final List<String> STORAGE_OBJECT_FILE_NAMES;//  = new ArrayList<String>();

    private List<DownloadedFiles> mStorageFiles = new ArrayList<DownloadedFiles>();

    JsonFactory JSON_FACTORY;
    HttpTransport mHttpTransport;
    GoogleCredential mCredential ;
    private static Storage mClient;

    public class DownloadedFiles
    {
        private String filename ;
        private ByteArrayOutputStream file;

        public DownloadedFiles(
                String filename,
                ByteArrayOutputStream file)
        {
            this.filename = filename ;
            this.file = file ;
        }

        public void setFile(ByteArrayOutputStream file) {
            this.file = file;
        }
        public void setFilename (String filename)
        {
            this.filename = filename ;
        }
        public String getFilename()
        {
            return this.filename ;
        }
        public ByteArrayOutputStream getFile()
        {
            return this.file;
        }
    }

    public CloudStorage(String applicationname,
                             String bucketname,
                             String emailaddress,
                             InputStream keystream,
                             String storepassword,
                             String alias,
                             String keypassword,
                             List<String> storageobjectfilenames) {
        this.APPLICATION_NAME = applicationname;
        this.BUCKET_NAME = bucketname ;
        this.EMAIL_ADRESS = emailaddress ;
        this.KEY_STREAM = keystream;
        this.STORE_PASSWORD = storepassword;
        this.ALIAS = alias ;
        this.KEY_PASSWORD = keypassword;
        this.STORAGE_OBJECT_FILE_NAMES = storageobjectfilenames ;
    }

    public void authCloud()
    {
        try
        {
            String emailAddress = EMAIL_ADRESS ;
            JSON_FACTORY = JacksonFactory.getDefaultInstance();
            mHttpTransport =  AndroidHttp.newCompatibleTransport();
            PrivateKey serviceAccountPrivateKey = SecurityUtils
                    .loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
                            KEY_STREAM,
                            STORE_PASSWORD,
                            ALIAS,
                            KEY_PASSWORD);
            mCredential = new GoogleCredential.Builder()
                    .setTransport(mHttpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(emailAddress)
                            //.setServiceAccountPrivateKeyFromP12File(new File(mMainActivity.getResources().getResourceEntryName(R.raw.key).))
                    .setServiceAccountPrivateKey(serviceAccountPrivateKey)
                    .setServiceAccountScopes(Collections.singleton("https://www.googleapis.com/auth/devstorage.read_only"))
                    .build();
            Log.d("XXCridential","Cridential Completed");
        }catch (Exception e)
        {
            Log.e("KeystoreEx",e.toString());
            e.printStackTrace();
        }
    }
    public void initBucket()
    {
        /*
        final android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                if ("gotit".equals(msg.getData().getString("message")))
                {
                    isThreadFinished = true ;
                    Log.d("XXmsg1",msg.getData().getString("message"));
                }
                Log.d("XXmsg2",msg.getData().getString("message"));
                return false;
            }
        });

        final Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("message","gotit");
        msg.setData(bundle);*/

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    authCloud();
                    // Set up global Storage instance.
                    mClient = new Storage.Builder(mHttpTransport, JSON_FACTORY, mCredential)
                            .setApplicationName(APPLICATION_NAME).build();
                    Log.d("XXClient", "Client initialized");

                    // Get metadata about the specified bucket.
                    Storage.Buckets.Get getBucket = mClient.buckets().get(BUCKET_NAME);
                    getBucket.setProjection("full");
                    Bucket bucket = getBucket.execute();

                    Log.d("name:", BUCKET_NAME);
                    Log.d("location:",bucket.getLocation());
                    Log.d("timeCreated:",bucket.getTimeCreated()+"");
                    Log.d("owner:",bucket.getOwner()+"");
                    Log.d("XXBucket", "Bucket initialized");

                    // List the contents of the bucket.
                    Storage.Objects.List listObjects = mClient.objects().list(BUCKET_NAME);
                    com.google.api.services.storage.model.Objects objects;
                    do {
                        objects = listObjects.execute();
                        List<StorageObject> items = objects.getItems();
                        if (null == items) {
                            //System.out.println("There were no objects in the given bucket; try adding some and re-running.");
                            Log.e("BucketEXP1", "There were no objects in the given bucket; try adding some and re-running.");
                            break;
                        }
                        for (StorageObject object : items) {
                            Log.d("BucketObjectName:", object.getName() + " (" + object.getSize() + " bytes)") ;
                            if (isRequiredFile(object.getName()))
                            {
                                Storage.Objects.Get get = mClient.objects().get(BUCKET_NAME,object.getName());
                                // Downloading data.
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                // If you're not in AppEngine, download the whole thing in one request, if possible.
                                get.getMediaHttpDownloader().setDirectDownloadEnabled(true);
                                get.executeMediaAndDownloadTo(out);
                                Log.d("DownloadJson", out.toString());
                                mStorageFiles.add(new DownloadedFiles(object.getName(),out));
                            }
                        }
                        listObjects.setPageToken(objects.getNextPageToken());
                    } while (null != objects.getNextPageToken());
                    //mHttpTransport.shutdown();
                } catch (IOException e) {
                    //System.err.println(e.getMessage());
                    //handler.sendMessage(msg) ;
                    Log.e("BucketEXP2",e.getMessage());
                } catch (Throwable t) {
                    //handler.sendMessage(msg) ;
                    t.printStackTrace();
                }
            }
        });
        thread.start();

        do {
            ;
        }
        while(thread.isAlive());
    }

    public boolean isRequiredFile(String filename)
    {
        for (String s : STORAGE_OBJECT_FILE_NAMES)
        {
            if(s.equals(filename))
            {
                return true ;
            }
        }
        return false ;
    }

    public List<DownloadedFiles> getStorageFiles()
    {
        return mStorageFiles ;
    }

    public static class Builder
    {
        private String APPLICATION_NAME ;//= "com.lonict.android.tonoradio";
        private String BUCKET_NAME ;// = "lonict_android";
        // private String BUCKET_OBJECT_NAME ;//="tono_american_radio/radiostations.json";
        private String EMAIL_ADRESS ;//="tono_american_radio/radiostations.json";
        private InputStream KEY_STREAM;
        private String STORE_PASSWORD;
        private String ALIAS;
        private String KEY_PASSWORD;
        private List<String> STORAGE_OBJECT_FILE_NAMES = new ArrayList<String>();

        //AuthCloudStorage authCloudStorage = new AuthCloudStorage();
        public Builder setApplicationName(String applicationName)
        {
            this.APPLICATION_NAME = applicationName ;
            return this ;
        }
        public Builder setBucketName (String bucketName)
        {
            this.BUCKET_NAME = bucketName;
            return this;
        }

        public Builder setEmailAdress (String emailAdress)
        {
            this.EMAIL_ADRESS = emailAdress ;
            return this ;
        }

        public Builder setKeyStream (InputStream keyStream)
        {
            this.KEY_STREAM = keyStream ;
            return this ;
        }
        public Builder setStorePass(String storePass)
        {
            this.STORE_PASSWORD = storePass ;
            return this ;
        }
        public Builder setAlias (String alias)
        {
            this.ALIAS = alias ;
            return this;
        }
        public Builder setKeyPassword(String keyPassword)
        {
            this.KEY_PASSWORD = keyPassword ;
            return this ;
        }
        public Builder setStorageObjectFiles(List<String> stringList)
        {
            this.STORAGE_OBJECT_FILE_NAMES = stringList;
            return this;
        }
        public CloudStorage build ()
        {
            return new CloudStorage(this.APPLICATION_NAME,
                    this.BUCKET_NAME,
                    this.EMAIL_ADRESS,
                    this.KEY_STREAM,
                    this.STORE_PASSWORD,
                    this.ALIAS,
                    this.KEY_PASSWORD,
                    this.STORAGE_OBJECT_FILE_NAMES
                    );
        }
    }
}
