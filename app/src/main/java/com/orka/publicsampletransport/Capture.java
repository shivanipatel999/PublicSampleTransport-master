package com.orka.publicsampletransport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Capture extends AppCompatActivity {
    int no=1;
    private Button mButton,btn;
    private ImageButton mim;
    private EditText info;
    private String iref;
    private ImageView imageView;
    private ImageView mImageView;
    private StorageReference mStorage;
    private ProgressDialog mprog;
    private static final int CAMERA_REQUEST_CODE=1;
    private DatabaseReference dbRef;
    DataSnapshot dataSnapshot;

    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        iref="";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
      btn=(Button) findViewById(R.id.button);
        info=(EditText) findViewById(R.id.infotext);
        mStorage= FirebaseStorage.getInstance().getReference();
        mButton=(Button) findViewById(R.id.UButton);
        mim=(ImageButton) findViewById(R.id.imgbtn);
        imageView=(ImageView)findViewById(R.id.imageview);
     // mImageView=(ImageView) findViewById(R.id.UimageView);
        mprog=new ProgressDialog(this);
        dbRef= FirebaseDatabase.getInstance().getReference("staff");
         id=dbRef.push().getKey();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_REQUEST_CODE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Find.class));
            }
        });
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void getString(final String s){
        iref=s;
        dbRef.child(id).child("images").child("img"+no).child("image").setValue(iref);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK) {

             mprog.setMessage("Uploading Image...");
            mprog.show();
            Uri uri = data.getData();
          //  if (uri == null) {
               // StorageReference filepath = mStorage.child("Photos.jpg");
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                uri=getImageUri(this,photo);
                final StorageReference filepath;
                mim.setImageURI(uri);
                filepath = mStorage.child("pics/pic2");
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String name="John";

                        Staff staff = new Staff(name,no);
                        dbRef.child(id).setValue(staff);
                         filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri urk) {
                                final String imgref=urk.toString();
                                getString(imgref);

                            }
                        });



                        dbRef.child(id).child("images").child("img"+no).child("Info").setValue(info.getText().toString());
                        dbRef.child(id).child("images").child("img"+no).child("Timestamp").setValue(ServerValue.TIMESTAMP);
                        mprog.dismiss();
                        Toast.makeText(Capture.this, "Uploading Finished...", Toast.LENGTH_SHORT).show();
                        dbRef.child(id).child("images").child("img"+no).child("image").setValue(iref);



                    }
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        mprog.dismiss();
                    }
                });
              /*  mim.setImageBitmap(photo);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dat = baos.toByteArray();

                UploadTask uploadTask = filepath.putBytes(dat);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        mprog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        mprog.dismiss();
                        Toast.makeText(Capture.this, "Uploading Finished...", Toast.LENGTH_SHORT).show();
                    }

                });*/
           /* }else {
                mim.setImageURI(uri);
                StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mprog.dismiss();
                        Toast.makeText(Capture.this, "Uploading Finished...", Toast.LENGTH_SHORT).show();
                    }
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        mprog.dismiss();
                    }
                });

                //mim.setImageURI(uri);
                //   mImageView.setImageURI(uri);
         /*
           StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mprog.dismiss();
                    Toast.makeText(Capture.this,"Uploading Finished...",Toast.LENGTH_SHORT).show();
                }
            });
            }*/
        }
    }
}
