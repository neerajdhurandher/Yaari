package com.example.apptrail4;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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


public class new_post_frg extends Fragment {



    EditText txtaboutphoto;
    TextView usernameTv, bioTv, currentTime;
    ImageView uploadImage, userDp;
    Button btnPost;
    FirebaseAuth fAuth;
    FirebaseUser currentUser;
    String currentUserUID, aboutPhoto_String;
    ProgressDialog pd;
    String usernametxt,userIamge_str,userIamge_str2,bio_get;

    String storagePermission[];
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;

    Uri uploadImageURI = null;




    public new_post_frg() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_new_post_frg, container, false);


        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
        currentUserUID = currentUser.getUid();

        pd = new ProgressDialog(getContext());

        usernameTv = view.findViewById(R.id.usernameS);
        bioTv = view.findViewById(R.id.bioS);
        currentTime = view.findViewById(R.id.timeS);
        txtaboutphoto = view.findViewById(R.id.textaboutimage);
        btnPost = view.findViewById(R.id.postbtn);
        uploadImage = view.findViewById(R.id.uploadImageId);
        userDp = view.findViewById(R.id.useriamgeS);


//        txtaboutphoto.setFocusable(false);

        uploadImage.isClickable();

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};



        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        final String currentdateTime = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();


        // set user detsils

        usernametxt = "" + currentUser.getDisplayName();

        userIamge_str = ""+ currentUser.getPhotoUrl();     // inuse


        usernameTv.setText(usernametxt);

        currentTime.setText(" at " + currentdateTime);


        try {
            Glide.with(getActivity()).load(userIamge_str).circleCrop().into(userDp);
        }
        catch (Exception e){
            Glide.with(getActivity()).load(R.drawable.user_icon).circleCrop().into(userDp);
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

                try {
                    uploadPost(aboutPhoto_String,uploadImageURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });








        return view;
    }



    private void uploadPost(final String aboutPhoto_string, Uri uri)  throws IOException  {

        pd.setMessage("Publishing...");
        pd.show();

        final String timestamp = String.valueOf(System.currentTimeMillis());

        String filepathAndname = "Posts/"+ "post_"+ currentUserUID +"_"+ timestamp;
        // we can add currentuser name in filename for better underastanding  and file handleling

        Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(),uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image_bitmap.compress(Bitmap.CompressFormat.PNG,30,baos);
        byte[] bytes_data = baos.toByteArray();

        StorageReference post_storageReference = FirebaseStorage.getInstance().getReference().child(filepathAndname);

        post_storageReference.putBytes(bytes_data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());

                String downloadUri = uriTask.getResult().toString();

                if (uriTask.isSuccessful()){

                    HashMap<Object,String> hashMap_post = new HashMap<>();

                    hashMap_post.put("uName",currentUser.getDisplayName());
                    hashMap_post.put("uEmail",currentUser.getEmail());
                    hashMap_post.put("uid",currentUserUID);
                    hashMap_post.put("uImage",currentUser.getPhotoUrl().toString());
                    hashMap_post.put("pDate_Time",timestamp);
                    hashMap_post.put("caption",aboutPhoto_string);
                    hashMap_post.put("uploadImage",downloadUri);
                    hashMap_post.put("pId",timestamp+"_"+currentUser.getUid());
                    hashMap_post.put("uBio",bio_get);

                    DatabaseReference databaseReference_post = FirebaseDatabase.getInstance().getReference("Posts");
                    DatabaseReference databaseReference_post_in_user_node = FirebaseDatabase.getInstance().getReference("Users").child(currentUserUID);

                    // add post_in_user_node
                    databaseReference_post_in_user_node.child(timestamp+"_"+currentUserUID).setValue(hashMap_post);

                    // add in post node

                    databaseReference_post.child(timestamp+"_"+currentUserUID).setValue(hashMap_post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Post Published", Toast.LENGTH_SHORT).show();


                            Intent updateandgohome = new Intent(getActivity(), HomeActivity.class);
                            startActivity(updateandgohome);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {

                uploadImageURI = data.getData();
                uploadImage.setPadding(5,5,5,5);
                uploadImage.setImageURI(uploadImageURI);

                txtaboutphoto.setFocusable(true);



            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkStoragePermission() {

        boolean result_SP;
        result_SP = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result_SP;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requsetStoragePermission() {

        ActivityCompat.requestPermissions(getActivity(),storagePermission, STORAGE_REQUEST_CODE);

//        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length> 0){
            boolean writeStorageAccpeted = grantResults[0] ==  PackageManager.PERMISSION_GRANTED;
            if(writeStorageAccpeted){
                pickFromGalley(); //permission graunted
            }
            else {

                Toast.makeText(getActivity(), "Please Enable Storage Permission",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void pickFromGalley() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);
    }


}
