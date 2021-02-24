package com.example.apptrail4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Personal_Chat_Activity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView sanmnevalekidp;
    TextView  samnevalekanaam,activity_status;
    EditText typedMessage;
    ImageButton send,attache_Image;

    String myUid,samnevaleuserkiUidget,samnevalekaphoto_string;
    List<ModelChat> chatList;
    AdapterChat adapterChat;
    LinearLayout samnevale_ka_details_layout;


    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentuser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDBref;
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    private String currentUserString;

    Uri imageuri = null ;


    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    public static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    String cameraPermission[];
    String storagePermission[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal__chat_);

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recycleview);
        samnevalekanaam = findViewById(R.id.samnevalekanaam_id);
        sanmnevalekidp = findViewById(R.id.samnevalekadp_id);
        activity_status =  findViewById(R.id.activity_status_id);
        typedMessage = findViewById(R.id.messagetype_id);
        send = findViewById(R.id.sendbtn_id);
        attache_Image = findViewById(R.id.attache_Image_btn_id);

        samnevale_ka_details_layout = findViewById(R.id.samnevale_ka_details_layout);


          // set chat layout in RecycleView
        LinearLayoutManager chatLayoutManger = new LinearLayoutManager(this);
        chatLayoutManger.setStackFromEnd(true);
        recyclerView.setLayoutManager(chatLayoutManger);

        Intent gotochat = getIntent();
        samnevaleuserkiUidget = gotochat.getStringExtra("samnevaleuserkiUid");


        mFirebaseAuth = FirebaseAuth.getInstance();
        currentuser =  mFirebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDBref = firebaseDatabase.getReference("Users");
        currentUserString = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        samnevale_ka_details_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Next_Person_Uid = samnevaleuserkiUidget;
                Intent gotoNextPersonProfile = new Intent(Personal_Chat_Activity.this,Next_User_Profile_Activity.class);
                gotoNextPersonProfile.putExtra("Next_Person_Uid_Var",Next_Person_Uid);
                startActivity(gotoNextPersonProfile);
            }
        });



        typedMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                String message = typedMessage.getText().toString().trim();

                if (TextUtils.isEmpty(message)){
                    attache_Image.setVisibility(View.VISIBLE);

                }
                else {
                    attache_Image.setVisibility(View.GONE);

                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                attache_Image.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                String message = typedMessage.getText().toString().trim();

                if (TextUtils.isEmpty(message)){
                    attache_Image.setVisibility(View.VISIBLE);

                }
                else {
                    attache_Image.setVisibility(View.GONE);

                }


            }
        });




// get samnevale user ki id ki details

        Query   userQuary = userDBref.orderByChild("uid").equalTo(samnevaleuserkiUidget);
        userQuary.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){

                    String samnevalekanaam_string = ""+ds.child("displayname").getValue();
                     samnevalekaphoto_string = ""+ds.child("image").getValue();

                     String onlineStatus = ""+ds.child("onlineStatus").getValue();

                     if (onlineStatus.equals("Online")){

                         activity_status.setText(onlineStatus);
                     }
                     else {
                         Calendar calendar =  Calendar.getInstance(Locale.ENGLISH);
                         calendar.setTimeInMillis(Long.parseLong(onlineStatus));
                         String dateTime = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();
                         activity_status.setText("Last seen at:"+dateTime);
                     }

                     // set next person's (samnevala) data on chat toolbar

                    samnevalekanaam.setText(samnevalekanaam_string);

                    try {
                        Glide.with(getApplicationContext()).load(samnevalekaphoto_string).circleCrop().into(sanmnevalekidp);
                    }
                    catch (Exception e){
                        Glide.with(getApplicationContext()).load(R.drawable.user_icon).circleCrop().into(sanmnevalekidp);
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       send.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String message = typedMessage.getText().toString().trim();

               if (TextUtils.isEmpty(message)){
                   Toast.makeText(Personal_Chat_Activity.this, "Type Something for Send", Toast.LENGTH_SHORT).show();

               }
               else{
                   sendMessage(message);
                   typedMessage.setText("");
                   attache_Image.setVisibility(View.VISIBLE);
               }
           }
       });

       attache_Image.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String optionfortakepic [] = {"Open Camera","Pick From Gallery"};
               AlertDialog.Builder alertdialog = new AlertDialog.Builder(Personal_Chat_Activity.this);
               // title of the Alert box
               alertdialog.setTitle("Choose Action");
               alertdialog.setItems(optionfortakepic, new DialogInterface.OnClickListener() {
                   @RequiresApi(api = Build.VERSION_CODES.M)
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if(which==0){
                           Toast.makeText(Personal_Chat_Activity.this, "Open Camera", Toast.LENGTH_SHORT).show();

                           // nahi bahi teri sakal kharab hai tu gallery se edited photo select kr

                           if(!checkCameraPermission()){
                               requsetCameraPermission();
                           }
                           else {
                               pickFromCamera();
                           }
                       }
                       else if (which==1){
                           Toast.makeText(Personal_Chat_Activity.this, "Pick From Gallery", Toast.LENGTH_SHORT).show();

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




        readMessage();

       seenMessage();
        
    }

    private void sendMessage(String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestampString = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> message_hashMap =  new HashMap<>();
        message_hashMap.put("sender",myUid);
        message_hashMap.put("reciver",samnevaleuserkiUidget);
        message_hashMap.put("message",message);
        message_hashMap.put("timestamp",timestampString );
        message_hashMap.put("isSeen","false");
        message_hashMap.put("message_type","text");

        databaseReference.child("Chats").push().setValue(message_hashMap);


        final DatabaseReference chatRef_MY_Next = FirebaseDatabase.getInstance().getReference("Chat_List")
                .child(myUid).child(samnevaleuserkiUidget);

          chatRef_MY_Next.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if(!snapshot.exists()){
                      chatRef_MY_Next.child("id").setValue(samnevaleuserkiUidget);
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });

        final DatabaseReference chatRef_Next_My = FirebaseDatabase.getInstance().getReference("Chat_List")
                .child(samnevaleuserkiUidget).child(myUid);
        chatRef_Next_My.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef_Next_My.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendImageMessage(Uri imageuri) throws IOException {

        final String timestamp = ""+System.currentTimeMillis();
        String fileAndPath_Name = "Chat_Images/"+"post_"+timestamp;

        // get bitMap to imageuri
        Bitmap  image_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageuri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image_bitmap.compress(Bitmap.CompressFormat.PNG,50,baos);
        byte[] bytes_data = baos.toByteArray();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(fileAndPath_Name);

        storageReference.putBytes(bytes_data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String download_URI = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){

                            DatabaseReference chat_image_uri_db = FirebaseDatabase.getInstance().getReference();

                            HashMap<String,Object> message_hashMap =  new HashMap<>();
                            message_hashMap.put("sender",myUid);
                            message_hashMap.put("reciver",samnevaleuserkiUidget);
                            message_hashMap.put("message",download_URI);
                            message_hashMap.put("timestamp",timestamp );
                            message_hashMap.put("message_type","image");
                            message_hashMap.put("isSeen","false");
                            chat_image_uri_db.child("Chats").push().setValue(message_hashMap);


                            final DatabaseReference chatRef_MY_Next = FirebaseDatabase.getInstance().getReference("Chat_List")
                                    .child(myUid).child(samnevaleuserkiUidget);

                            chatRef_MY_Next.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists()){
                                        chatRef_MY_Next.child("id").setValue(samnevaleuserkiUidget);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            final DatabaseReference chatRef_Next_My = FirebaseDatabase.getInstance().getReference("Chat_List")
                                    .child(samnevaleuserkiUidget).child(myUid);
                            chatRef_Next_My.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()){
                                        chatRef_Next_My.child("id").setValue(myUid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            // send Notifiacation




                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });







    }



    private void seenMessage() {

        userRefForSeen =  FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelChat chat = ds.getValue(ModelChat.class);

                    if (chat.getReciver().equals(myUid) && chat.getSender().equals(samnevaleuserkiUidget)){

                        HashMap<String,Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen","true");

                        ds.getRef().updateChildren(hasSeenHashMap);


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessage() {

        chatList = new ArrayList<>();
        DatabaseReference dbrefReadMessage = FirebaseDatabase.getInstance().getReference("Chats");
        dbrefReadMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelChat chat = ds.getValue(ModelChat.class);
                    assert chat != null;
                    if (chat.getReciver().equals(myUid) && chat.getSender().equals(samnevaleuserkiUidget) ||
                            chat.getReciver().equals(samnevaleuserkiUidget) && chat.getSender().equals(myUid)){

                        chatList.add(chat);

                    }

                    adapterChat = new AdapterChat(Personal_Chat_Activity.this,chatList,samnevalekaphoto_string);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkOnlineStatus(String status){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap <String, Object> hashMap_OnlineStatus = new HashMap<>();
        hashMap_OnlineStatus.put("onlineStatus",status);

        databaseReference.child(myUid).updateChildren(hashMap_OnlineStatus);


    }


    private boolean checkStoragePermission() {

        boolean resultA;
        resultA= ContextCompat.checkSelfPermission(Personal_Chat_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resultA;
    }

    private boolean checkCameraPermission(){

        boolean result2 = ContextCompat.checkSelfPermission(Personal_Chat_Activity.this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result3 = ContextCompat.checkSelfPermission(Personal_Chat_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

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

                        Toast.makeText(Personal_Chat_Activity.this, "Please Enable Camera & Storage Permission",Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(Personal_Chat_Activity.this, "Please Enable Storage Permission",Toast.LENGTH_SHORT).show();
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

                try {
                    sendImageMessage(imageuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){

                try {
                    sendImageMessage(imageuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }



        super.onActivityResult(requestCode, resultCode, data);
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

        imageuri = Personal_Chat_Activity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);

    }



    @Override
    protected void onStart() {
        Checkuser();
        checkOnlineStatus("Online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        set timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);

        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("Online");
        super.onResume();
    }

    private void Checkuser() {

         currentuser =  mFirebaseAuth.getCurrentUser();

        if (currentuser != null){

            myUid = currentuser.getUid();



        }
        else {
            startActivity( new  Intent (this,LoginActivity.class));
            Toast.makeText(this, ""+myUid+"  ", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, ""+samnevaleuserkiUidget, Toast.LENGTH_SHORT).show();
            finish();

        }
    }


}
