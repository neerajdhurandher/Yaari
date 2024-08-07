package com.example.apptrail4;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class Add_Story_Activity extends AppCompatActivity {

    private Uri story_Iamge_uri;
    String story_URL = "";
    private StorageTask storageTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__story_);

        storageReference = FirebaseStorage.getInstance().getReference("Story");

        CropImage.activity().setAspectRatio(9,16)
                .start(Add_Story_Activity.this);


    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void publishStory(){

        final ProgressDialog pd=  new ProgressDialog(this);
        pd.setMessage("Posting");
        pd.show();

        if (story_Iamge_uri != null){
            final StorageReference imageREf = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(story_Iamge_uri));

            storageTask = imageREf.putFile(story_Iamge_uri);

            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageREf.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        story_URL = downloadUri.toString();

                        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference savestory_db = FirebaseDatabase.getInstance().getReference("Story").child(currentUserUID);

                        String story_Id = savestory_db.push().getKey();

                        long time_end = System.currentTimeMillis()+86400000; // valid 1day,  1 sec = 1000 milis

                        HashMap<String,Object> story_hashmap = new HashMap<>();
                        story_hashmap.put("image_url",story_URL);
                        story_hashmap.put("time_start", ServerValue.TIMESTAMP);
                        story_hashmap.put("time_end",time_end);
                        story_hashmap.put("story_Id",story_Id);
                        story_hashmap.put("user_Id",currentUserUID);

                        savestory_db.child(story_Id).setValue(story_hashmap);
                        pd.dismiss();
                        finish();

                    }

                    else {
                        Toast.makeText(Add_Story_Activity.this, "Failed to Upload Story", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Add_Story_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }
        else {
            Toast.makeText(Add_Story_Activity.this, "No Image Selected ! ", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            story_Iamge_uri = result.getUri();

            publishStory();
        }
        else {
            Toast.makeText(Add_Story_Activity.this, "Something gone Wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Add_Story_Activity.this, HomeActivity.class));
            finish();

        }
    }
}