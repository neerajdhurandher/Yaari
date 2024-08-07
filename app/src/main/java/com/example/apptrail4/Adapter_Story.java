package com.example.apptrail4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Adapter_Story extends  RecyclerView.Adapter<Adapter_Story.StoryHolder>{

    private Context context;
    private List<ModelStory> story_list;

    public Adapter_Story(Context context, List<ModelStory> story_list) {
        this.context = context;
        this.story_list = story_list;
    }

    @NonNull
    @Override
    public StoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {



        if (i == 0){

            View view = LayoutInflater.from(context).inflate(R.layout.row_add_story_view,viewGroup,false);
            return new Adapter_Story.StoryHolder(view);

        }
        else {

            View view = LayoutInflater.from(context).inflate(R.layout.row_story_view,viewGroup,false);
            return new Adapter_Story.StoryHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final StoryHolder holder, int i) {

        final ModelStory modelStory = story_list.get(i);

        userInfo(holder,modelStory.getUser_Id(),i);

        if (holder.getAdapterPosition() !=0){

            seenStory(holder,modelStory.getUser_Id());

        }

        if (holder.getAdapterPosition() ==0){

        myStory(holder.story_add_txt,holder.story_add,false);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0) {
                    myStory(holder.story_add_txt,holder.story_add,true);
                }
                else{
                   // to do go to to story
                    Intent gotoStory = new Intent(context,Story_view_Activity.class);
                    gotoStory.putExtra("userid",modelStory.getUser_Id());
                    context.startActivity(gotoStory);
                }
            }
        });







        }

    @Override
    public int getItemCount() {
        return story_list.size();
    }



    public class  StoryHolder extends RecyclerView.ViewHolder{

        public ImageView story_useriamgeS, story_add, story_useriamge_seen;
       public  TextView story_username, story_add_txt;

        public StoryHolder(@NonNull View itemView) {
            super(itemView);

            story_useriamgeS = itemView.findViewById(R.id.story_useriamgeS);
            story_useriamge_seen = itemView.findViewById(R.id.story_useriamge_seen);
            story_add = itemView.findViewById(R.id.story_add);

            story_username = itemView.findViewById(R.id.story_username);
            story_add_txt = itemView.findViewById(R.id.story_add_txt);


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return  0;
        }
        return 1;
    }

    private void userInfo(final StoryHolder viewHolder, String user_Id, final int pos ){

        DatabaseReference user_db = FirebaseDatabase.getInstance().getReference("Users").child(user_Id);

        user_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ModelUser user = snapshot.getValue(ModelUser.class);

                try {
                    Glide.with(context).load(user.getImage()).circleCrop().into(viewHolder.story_useriamgeS);

                }
                catch (Exception e) {
                    Glide.with(context).load(R.drawable.user_icon).circleCrop().into(viewHolder.story_useriamgeS);

                }

                viewHolder.story_useriamgeS.setVisibility(View.VISIBLE);

                if (pos != 0){

                    try {
                        Glide.with(context).load(user.getImage()).circleCrop().into(viewHolder.story_useriamge_seen);

                    }
                    catch (Exception e) {
                        Glide.with(context).load(R.drawable.user_icon).circleCrop().into(viewHolder.story_useriamge_seen);

                    }

                    viewHolder.story_username.setText(user.getDisplayname());
                    viewHolder.story_useriamge_seen.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void myStory(final TextView textView, final ImageView imageView, final boolean click){

        DatabaseReference myStory_db = FirebaseDatabase.getInstance().getReference("Story").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myStory_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count = 0;
                long timecurent = System.currentTimeMillis();

                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelStory modelStory = ds.getValue(ModelStory.class );

                    if (timecurent > modelStory.getTime_start() && timecurent< modelStory.getTime_end()){

                        count ++;
                    }

                }

                if (click){

                   if (count>0){
                       AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                       alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               // go to story

                               Intent gotoStory = new Intent(context,Story_view_Activity.class);
                               gotoStory.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                               context.startActivity(gotoStory);
                               dialog.dismiss();
                           }
                       });

                       alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               Intent goto_addStory = new Intent(context, Add_Story_Activity.class);
                               context.startActivity(goto_addStory);
                               dialog.dismiss();

                           }
                       });
                       alertDialog.show();
                   }

                   else {
                       Intent goto_addStory = new Intent(context,Add_Story_Activity.class);
                       context.startActivity(goto_addStory);
                   }

                }

                else {
                    if (count>0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }
                    else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private  void  seenStory(final StoryHolder viewHolder, String userId){

        DatabaseReference seenStory_db = FirebaseDatabase.getInstance().getReference("Story").child(userId);
        seenStory_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int i = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    if (!ds.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                            && System.currentTimeMillis() < ds.getValue(ModelStory.class).getTime_end()){
                        i++;
                    }
                }

                if (i>0){
                    viewHolder.story_useriamgeS.setVisibility(View.VISIBLE);
                    viewHolder.story_useriamge_seen.setVisibility(View.GONE);
                }
                else {

                    viewHolder.story_useriamgeS.setVisibility(View.GONE);
                    viewHolder.story_useriamge_seen.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



}
