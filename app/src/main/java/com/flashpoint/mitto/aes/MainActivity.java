package com.flashpoint.mitto.aes;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import android.app.ProgressDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button upload,choose;
    String FileName;
    String FileExtension;
    File sdcard;
    Uri filePath;
    int flag=0;
    private StorageReference storageReference;
    private static final int PICKFILE_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upload = (Button) findViewById(R.id.upload);
        choose = (Button) findViewById(R.id.choose);
        sdcard = Environment.getExternalStorageDirectory();

        storageReference = FirebaseStorage.getInstance().getReference();
        upload.setOnClickListener(this);
        choose.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == upload) {


            File inputFile = new File(sdcard,FileName);
            File encryptedFile = new File(sdcard, FileName+".encrypted");
            File decryptedFile = new File(sdcard,FileName);
            filePath=Uri.fromFile(new File(sdcard,FileName+".encrypted"));
            try {
                TestFileEncryption.encrypt(inputFile, encryptedFile);
                Toast.makeText(MainActivity.this, "Encrypted successfully", Toast.LENGTH_LONG).show();
                TestFileEncryption.decrypt(encryptedFile, decryptedFile);
                Toast.makeText(MainActivity.this, "Decrypted successfully", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            uploadFile();
        }
        if (v == choose) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICKFILE_RESULT_CODE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                 //   filePath=sdcard+"\"DecryptedFile"+FileExtension;

                    FileName = data.getData().getLastPathSegment().replace("primary:","");
                    Log.e("Filename", FileName);
                    FileExtension=FileName.substring(FileName.lastIndexOf("."));
                   Log.e("Extension",FileExtension);
                }
        }
    }
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = storageReference.child(FileName+".encrypted");
            //i++;
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");


                        }
                    });

        }
        //if there is not any file
        else {
            //you can display an error toast
            Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_LONG).show();
        }
    }


}