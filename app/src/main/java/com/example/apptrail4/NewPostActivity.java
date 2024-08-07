package com.example.apptrail4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class NewPostActivity extends AppCompatActivity {

    EditText txtaboutphoto;
    TextView usernameTv, bioTv, currentTime;
    ImageView uploadImage, userDp;
    Button btnPost;
    FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String currentUserUID, aboutPhoto_String;
    ProgressDialog pd;
    String userNameTxt,userImageStr,bio_get;

    String storagePermission[];
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final String TAG = "NewPostActivity";

    Uri uploadImageURI = null;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naya_poost);


        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
        currentUserUID = currentUser.getUid();

        pd = new ProgressDialog(this);

        usernameTv = findViewById(R.id.usernameS);
        bioTv = findViewById(R.id.bioS);
        currentTime = findViewById(R.id.timeS);
        txtaboutphoto = findViewById(R.id.textaboutimage);
        btnPost = findViewById(R.id.postbtn);
        uploadImage = findViewById(R.id.uploadImageId);
        userDp = findViewById(R.id.useriamgeS);


//        txtaboutphoto.setFocusable(false);

        uploadImage.isClickable();

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};



        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        final String currentdateTime = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();


        // set user detsils

        userNameTxt = "" + currentUser.getDisplayName();

        userImageStr = ""+ currentUser.getPhotoUrl();     // inuse


        usernameTv.setText(userNameTxt);

        currentTime.setText(" at " + currentdateTime);


        try {
            Glide.with(getApplicationContext()).load(userImageStr).circleCrop().into(userDp);
        }
        catch (Exception e){
            Glide.with(getApplicationContext()).load(R.drawable.user_icon).circleCrop().into(userDp);
        }


         DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserUID);

         userDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {


                    if (snapshot.child("About").child("bio").hasChild("bio")) {

                        String biotxt = "" + snapshot.child("About").child("bio").child("bio").getValue();
                        bio_get = biotxt;

                        if (!biotxt.isEmpty()) {

                            bioTv.setText(biotxt);
                        }

                        else {

                            bioTv.setVisibility(View.GONE);
                        }

                    }

                    else {
                        bioTv.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

          uploadImage.setOnClickListener(new View.OnClickListener() {
              @RequiresApi(api = Build.VERSION_CODES.M)
              @Override
              public void onClick(View v) {

                  //get Image from Gallery

                  if (!checkStoragePermission()) {
                      requsetStoragePermission();
                  } else {
                      pickFromGalley();
                  }

              }
          });


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutPhoto_String = txtaboutphoto.getText().toString();
                uploadPost(aboutPhoto_String,uploadImageURI);
            }
        });






    }

    private void uploadPost(final String aboutPhoto_string, Uri uri) {

        pd.setMessage("Publishing...");
        pd.show();

        final String timestamp = String.valueOf(System.currentTimeMillis());

        String filepathAndName = "Posts/"+ "post_"+ currentUserUID +"_"+ timestamp;
        // we can add current user name in filename for better understanding  and file handling

        byte[] img_bytes_data;
        try {
            Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image_bitmap.compress(Bitmap.CompressFormat.PNG, 30, baos);
            img_bytes_data = baos.toByteArray();
        }catch (IOException ioException){
            Log.e(TAG,"Error :- "+ioException.getMessage());
            Toast.makeText(getApplicationContext(),"Image uploading failed",Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference post_storageReference = FirebaseStorage.getInstance().getReference().child(filepathAndName);

        post_storageReference.putBytes(img_bytes_data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());

                String downloadUri = uriTask.getResult().toString();

                if (uriTask.isSuccessful()){

                    HashMap<Object,String> hashMap_post = new HashMap<>();

                    hashMap_post.put("uName",currentUser.getDisplayName());
                    hashMap_post.put("uEmail",currentUser.getEmail());
                    hashMap_post.put("uid",currentUser.getUid());
                    hashMap_post.put("uImage",currentUser.getPhotoUrl().toString());
                    hashMap_post.put("pDate_Time",timestamp);
                    hashMap_post.put("caption",aboutPhoto_string);
                    hashMap_post.put("uploadImage",downloadUri);
                    hashMap_post.put("pId",timestamp+"_"+currentUser.getUid());
                    hashMap_post.put("uBio",bio_get);

                    DatabaseReference databaseReference_post = FirebaseDatabase.getInstance().getReference("Posts");

                     databaseReference_post.child(timestamp+"_"+currentUserUID).setValue(hashMap_post).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid) {
                             pd.dismiss();
                             Toast.makeText(NewPostActivity.this, "Post Published", Toast.LENGTH_SHORT).show();


                                Intent updateandgohome = new Intent(NewPostActivity.this, HomeActivity.class);
                                   startActivity(updateandgohome);
                                   finish();

                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             pd.dismiss();
                             Toast.makeText(NewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                         }
                     });

                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(NewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {

                uploadImageURI = data.getData();
                uploadImage.setPadding(10,3,10,3);
                uploadImage.setImageURI(uploadImageURI);

                txtaboutphoto.setFocusable(true);



            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkStoragePermission() {

        boolean result_SP;
        result_SP = ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result_SP;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requsetStoragePermission() {

        ActivityCompat.requestPermissions(this,storagePermission, STORAGE_REQUEST_CODE);

//        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length> 0){
            boolean writeStorageAccpeted = grantResults[0] ==  PackageManager.PERMISSION_GRANTED;
            if(writeStorageAccpeted){
                pickFromGalley(); //permission graunted
            }
            else {

                Toast.makeText(NewPostActivity.this, "Please Enable Storage Permission",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void pickFromGalley() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);
    }


}
