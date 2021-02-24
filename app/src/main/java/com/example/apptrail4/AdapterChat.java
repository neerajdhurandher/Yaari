package com.example.apptrail4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MessageHolder> {

private static final int MSG_TYPE_LEFT = 0;
private static final int MSG_TYPE_RIGHT = 1;
Context context;
List<ModelChat> chatList;
String imageUri;
    private FirebaseUser currentUser;



    public AdapterChat(Context context, List<ModelChat> chatList, String imageUri) {
        this.context = context;
        this.chatList = chatList;
        this.imageUri = imageUri;
    }



    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        if (i == MSG_TYPE_RIGHT){

            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_sender_right,viewGroup,false);
            return new MessageHolder(view);

        }
        else {

            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_reciver_left,viewGroup,false);
            return new MessageHolder(view);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int i) {

        String messageString = ""+chatList.get(i).getMessage();
        String timestampString = ""+chatList.get(i).getTimestamp();
        String message_typeString = ""+chatList.get(i).getMessage_type();
        String seen_status_str = ""+chatList.get(i).isSeen;

        // set time formate type

        Calendar calendar =  Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestampString));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

        // try to add todsy yestarday in message timestamp                                               

//        String date_m = DateFormat.format("dd/MM/yyyy ",calendar).toString();
//
//        final String present_Time_get = String.valueOf(System.currentTimeMillis());
//        Calendar calendar_present =  Calendar.getInstance(Locale.ENGLISH);
//        calendar_present.setTimeInMillis(Long.parseLong(present_Time_get));
//        String date_c = DateFormat.format("dd/MM/yyyy ",calendar).toString();
//
//
//
//
//
//
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
////        Date message_Time = (Date) DateFormat.format("dd/MM/yyyy ", Long.parseLong(date_m));
////        Date current_Time = (Date) DateFormat.format("dd/MM/yyyy ", Long.parseLong(date_c));
//
//          Date message_Time  = calendar.getTime();
//         Date current_Time  = calendar_present.getTime();
//
//
//        System.out.println("The message_Time is: " + sdformat.format(message_Time));
//        System.out.println("The current_Time is: " + sdformat.format(current_Time));
//
//        if(message_Time.compareTo(current_Time) > 0) {
//
//            System.out.println("message_Time occurs after current_Time");
//            holder.time_var.setText("Not Possible");
//        }
//        else if(message_Time.compareTo(current_Time) < 1) {
//
//            System.out.println("message_Time occurs before current_Time");
//            holder.time_var.setText(dateTime);
//
//
//        }
//
//        else if(message_Time.compareTo(current_Time ) == 1) {
//
//            System.out.println("message_Time occurs before current_Time");
//            holder.time_var.setText("Yesterday");
//
//
//        }
//
//        else if(message_Time.equals(current_Time)) {
//            System.out.println("Both dates are equal");
//            holder.time_var.setText("Today");
//        }





        holder.time_var.setText(dateTime);

        try {
            Glide.with(context).load(imageUri).circleCrop().into(holder.reciverDP_var);

        }
        catch (Exception e) {
            Glide.with(context).load(R.drawable.user_icon).circleCrop().into(holder.reciverDP_var);

        }


       if (message_typeString.equals("text")) {


           holder.message_var.setText(messageString);
           holder.Image_Message.setVisibility(View.GONE);


       }



        if (message_typeString.equals("image")) {

            holder.message_var.setVisibility(View.GONE);
            holder.Image_Message.setVisibility(View.VISIBLE);

            try {
                Glide.with(context).load(messageString).into(holder.Image_Message);

            }
            catch (Exception e) {
                Glide.with(context).load(R.drawable.ic_image).into(holder.Image_Message);

            }



        }




    // check seen status

        if (i==chatList.size()-1){

            if (seen_status_str.equals("true")){

                holder.isSeen_var.setText("seen");
            }
            if (seen_status_str.equals("false")){

                holder.isSeen_var.setText("delivered");

            }

        }

        else {
            holder.isSeen_var.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(currentUser.getUid())){

            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }



    }

    class MessageHolder extends RecyclerView.ViewHolder{


        ImageView reciverDP_var,Image_Message;
        TextView message_var , time_var, isSeen_var;


        public MessageHolder(@NonNull View messageview){
            super(messageview);

            reciverDP_var  = messageview.findViewById(R.id.reciverDP);
            message_var  = messageview.findViewById(R.id.text_Message);
            time_var = messageview.findViewById(R.id.message_time);
            isSeen_var= messageview.findViewById(R.id.seenId);
            Image_Message= messageview.findViewById(R.id.image_Message);





        }
    }

//    public static class Adapter_Pnd_Foll_Req extends RecyclerView.Adapter<Adapter_Pnd_Foll_Req.userViewHolder> {
//
//        Context context;
//        List<ModelUser> userList;
//
//        public Adapter_Pnd_Foll_Req(Context context, List<ModelUser> userList) {
//            this.context = context;
//            this.userList = userList;
//        }
//
//
//
//
//        @NonNull
//        @Override
//        public userViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//
//            View view = LayoutInflater.from(context).inflate(R.layout.row_req_pending_users, viewGroup,false);
//            return new userViewHolder(view) ;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull userViewHolder holder, int i) {
//
//
//            //fetch data
//            final String userUid = userList.get(i).getUid();
//            String userImageF = userList.get(i).getImage();
//            final String userdisplaynameF = userList.get(i).getDisplayname();
//            String useremailF = userList.get(i).getEmail();
//
//            // assign data
//            holder.usernameA.setText(userdisplaynameF);
//            holder.useremailA.setText(useremailF);
//
//            try {
//                Glide.with(context).load(userImageF).circleCrop().into(holder.userImageA);
//            }
//            catch (Exception e){
//                Glide.with(context).load(R.drawable.user_icon).circleCrop().into(holder.userImageA);
//            }
//
//
//
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Toast.makeText(context, ""+userdisplaynameF, Toast.LENGTH_SHORT).show();
//
//    //                Intent gotochat = new Intent(context,Personal_Chat_Activity.class);
//    //                gotochat.putExtra("samnevaleuserkiUid",userUid);
//    //                context.startActivity(gotochat);
//
//                    String Next_Person_Uid = userUid;
//                    Intent gotoNextPersonProfile = new Intent(context, Next_User_Profile_Activity.class);
//                    gotoNextPersonProfile.putExtra("Next_Person_Uid_Var",Next_Person_Uid);
//                    context.startActivity(gotoNextPersonProfile);
//
//                }
//            });
//
//            holder.acceptbtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//            holder.declinebtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return userList.size();
//        }
//
//        class userViewHolder extends RecyclerView.ViewHolder{
//            ImageView userImageA;
//            TextView usernameA,useremailA;
//            Button acceptbtn,declinebtn;
//
//
//            public userViewHolder(@NonNull View itemView) {
//                super(itemView);
//
//                userImageA = itemView.findViewById(R.id.useriamgeS);
//                usernameA = itemView.findViewById(R.id.usernameS);
//                useremailA = itemView.findViewById(R.id.useremailS);
//                acceptbtn= itemView.findViewById(R.id.acceptbtnid);
//                declinebtn= itemView.findViewById(R.id.declinebtnid);
//
//
//
//
//            }
//        }
//
//
//
//
//    }
}
