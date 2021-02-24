package com.example.apptrail4;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class Edit_Profile_Activity extends AppCompatActivity {
    EditText username,bio,dob,city,interet,relationstatus;
    Button savebtn;
    ImageButton imageButton,coverImageButton;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase Fdatabase;
    private DatabaseReference userRef;
    private StorageReference storageReference;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String USER = "users";

      String emailstr;
      String storagepath = "User_Profilr_Images/";
       Uri imageuri = null ;
       ProgressDialog progressDialog;

    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    public static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    String cameraPermission[];
    String storagePermission[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile_);


        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        Fdatabase = FirebaseDatabase.getInstance();
        userRef = Fdatabase.getReference("users");
        storageReference = getInstance().getReference();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        savebtn        = findViewById(R.id.btnsave);
        username       = findViewById(R.id.edit_username);
        bio            = findViewById(R.id.edit_bio);
        dob            = findViewById(R.id.edit_dob);
        city           = findViewById(R.id.edit_city);
        interet        = findViewById(R.id.edit_interest);
        relationstatus = findViewById(R.id.edit_rel_status);

        imageButton    = findViewById(R.id.edit_pic);
        coverImageButton = findViewById(R.id.userCover_ImageId);

        progressDialog = new ProgressDialog(this);



        savebtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String usernametxt = username.getText().toString();
                String biotxt = bio.getText().toString();
                String dobtxt = dob.getText().toString();
                String citytxt = city.getText().toString();
                String interettxt = interet.getText().toString();
                String relationstatustxt = relationstatus.getText().toString();
              String uid = currentUser.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                 DatabaseReference reference = database.getReference("Users");


                 HashMap<String,String> usernamehashMap = new HashMap<>();
                if (!usernametxt.isEmpty()) {
                    usernamehashMap.put("username", usernametxt);
                reference.child(uid).child("About").child("username").setValue(usernamehashMap);
                }



                 HashMap<String,String> biohashMap = new HashMap<>();
                if(!biotxt.isEmpty()){
                    biohashMap.put("bio",biotxt);
                reference.child(uid).child("About").child("bio").setValue(biohashMap);
                }



                HashMap<String,String> dobhashMap = new HashMap<>();
                if(!dobtxt.isEmpty()){
                    dobhashMap.put("dob",dobtxt);
                reference.child(uid).child("About").child("dob").setValue(dobhashMap);}


                HashMap<String,String> cityhashMap = new HashMap<>();
                if (!citytxt.isEmpty()){
                    cityhashMap.put("city", citytxt);
                reference.child(uid).child("About").child("city").setValue(cityhashMap);}


                HashMap<String,String> intresthashMap = new HashMap<>();
                if (!interettxt.isEmpty()){
                    intresthashMap.put("intrest",interettxt);
                reference.child(uid).child("About").child("intrest").setValue(intresthashMap);}


                HashMap<String,String> relSthashMap = new HashMap<>();
                if (!relationstatustxt.isEmpty()){
                    relSthashMap.put("relationshipstatus",relationstatustxt);
                reference.child(uid).child("About").child("relationshipstatus").setValue(relSthashMap);
                }




                Intent updateandgohome = new Intent(Edit_Profile_Activity.this,HomeActivity.class);
                startActivity(updateandgohome);
                finish();


            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String optionfortakepic [] = {"Open Camera","Pick From Gallery"};
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(Edit_Profile_Activity.this);
                // title of the Alert box
                alertdialog.setTitle("Choose Action");
                alertdialog.setItems(optionfortakepic, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            Toast.makeText(Edit_Profile_Activity.this, "Open Camera", Toast.LENGTH_SHORT).show();

                            // nahi bahi teri sakal kharab hai tu gallery se edited photo select kr

                            if(!checkCameraPermission()){
                                requsetCameraPermission();
                            }
                            else {
                                pickFromCamera();
                            }
                        }
                        else if (which==1){
                            Toast.makeText(Edit_Profile_Activity.this, "Pick From Gallery", Toast.LENGTH_SHORT).show();

                            if (!checkStoragePermission()){
                                requsetStoragePermission();
                            }
                            else {
                                pickFromGalley();
                            }
                        }
                    }
                });
                alertdialog.create().show();

            }
        });







    }

    private boolean checkStoragePermission() {

        boolean resultA;
        resultA= ContextCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resultA;
    }

    private boolean checkCameraPermission(){

        boolean result2 = ContextCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result3 = ContextCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return  result2 && result3;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private  void requsetStoragePermission(){
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private   void requsetCameraPermission(){
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length>0){
                    boolean cameraAccpeted = grantResults[0] ==  PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccpeted = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if(cameraAccpeted && writeStorageAccpeted){
                        pickFromCamera(); //permission graunted
                    }
                    else {

                        Toast.makeText(Edit_Profile_Activity.this, "Please Enable Camera & Storage Permission",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length> 0){
                    boolean writeStorageAccpeted = grantResults[0] ==  PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccpeted){
                        pickFromGalley(); //permission graunted
                    }
                    else {

                        Toast.makeText(Edit_Profile_Activity.this, "Please Enable Storage Permission",Toast.LENGTH_SHORT).show();
                    }
                }

            }
           break;
        }



//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //called after pick image from camera or gallery

        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
                imageuri = data.getData();

                imageButton.setImageURI(imageuri);





               UploadProfilePhoto(imageuri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){

//                Intent updateandgohome = new Intent(Edit_Profile_Activity.this,MainActivity.class);
//              startActivity(updateandgohome);

                imageButton.setImageURI(imageuri);

                UploadProfilePhoto(imageuri);
            }

        }



        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadProfilePhoto(Uri imageuri) {

        progressDialog.show();

        String fileAndpathname = storagepath +""+ currentUser.getUid();
        StorageReference storageReference2nd = storageReference.child(fileAndpathname);
        storageReference2nd.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloaduri = uriTask.getResult();
                if ((uriTask.isSuccessful())){
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("New image", downloaduri.toString());  // put the new uri in image hashMap value
                    Toast.makeText(Edit_Profile_Activity.this, "New Image Uri Updated", Toast.LENGTH_SHORT).show();
                    userRef.child(currentUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(Edit_Profile_Activity.this, "Picture Updated..", Toast.LENGTH_SHORT).show();
//                            Intent updateandgohome = new Intent(Edit_Profile_Activity.this,MainActivity.class);
//                            startActivity(updateandgohome);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Edit_Profile_Activity.this, "Error Picture Updating..", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
                else {
                    progressDialog.dismiss();

                    Toast.makeText(Edit_Profile_Activity.this,"Some Error Occured",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Edit_Profile_Activity.this,e.getMessage(),Toast.LENGTH_SHORT);

            }
        });

    }

    private void pickFromGalley() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
    values.put(MediaStore.Images.Media.TITLE, "Temp pic");
    values.put(MediaStore.Images.Media.DESCRIPTION,"Temp pic Description");
    imageuri = Edit_Profile_Activity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);

    }
}
